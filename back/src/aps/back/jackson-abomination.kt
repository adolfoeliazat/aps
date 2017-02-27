package aps.back

import aps.*
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.*
import com.fasterxml.jackson.databind.deser.std.*
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.ser.*
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.*
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.*
import com.fasterxml.jackson.databind.module.SimpleModule
import into.kommon.*
import java.io.IOException
import kotlin.reflect.KClass

val objectMapper = ObjectMapper()-{o->
    o.enableDefaultTyping(NON_FINAL, JsonTypeInfo.As.PROPERTY)
}

val objectFieldObjectMapper = ObjectMapper()-{mapper->
    val typer = ObjectMapper.DefaultTypeResolverBuilder(NON_FINAL)
        .init(JsonTypeInfo.Id.CLASS, null)
        .inclusion(JsonTypeInfo.As.PROPERTY)
        .typeProperty("\$\$\$class")
    mapper.setDefaultTyping(typer)
    mapper.registerModule(SimpleModule()-{module->
        module.addDeserializer(KClass::class.java, object:StdDeserializer<KClass<*>>(null as Class<*>?) {
            override fun deserialize(jp: JsonParser, ctx: DeserializationContext): KClass<*> {
                val node = jp.codec.readTree<JsonNode>(jp)
                val simpleName = node.get("simpleName").asText()
                return Class.forName("aps.$simpleName").kotlin
            }
        })
    })
}

val shittyObjectMapper = object:ObjectMapper() {

    override fun createDeserializationContext(p: JsonParser, cfg: DeserializationConfig): DefaultDeserializationContext {
        class FuckingContext : DefaultDeserializationContext {

            constructor(df: DeserializerFactory) : super(df, null)
            private constructor(src: FuckingContext, config: DeserializationConfig, jp: JsonParser, values: InjectableValues?) : super(src, config, jp, values)
            private constructor(src: FuckingContext) : super(src)
            private constructor(src: FuckingContext, factory: DeserializerFactory) : super(src, factory)

            override fun copy(): DefaultDeserializationContext {
                if (javaClass != FuckingContext::class.java) {
                    return super.copy()
                }
                return FuckingContext(this)
            }

            override fun createInstance(config: DeserializationConfig,
                                        jp: JsonParser, values: InjectableValues?): DefaultDeserializationContext {
                return FuckingContext(this, config, jp, values)
            }

            override fun with(factory: DeserializerFactory): DefaultDeserializationContext {
                return FuckingContext(this, factory)
            }

            @Throws(IOException::class)
            override fun handleUnexpectedToken(instClass: Class<*>, t: JsonToken?,
                                               p: JsonParser,
                                               msg: String?, vararg msgArgs: Any): Any? {
                var msg = msg
                if (msgArgs.size > 0) {
                    msg = String.format(msg!!, *msgArgs)
                }
                var h: LinkedNode<DeserializationProblemHandler>? = _config.problemHandlers
                while (h != null) {
                    val instance = h.value().handleUnexpectedToken(this,
                                                                   instClass, t, p, msg)
                    if (instance !== DeserializationProblemHandler.NOT_HANDLED) {
                        if (instance == null
                            || instClass.isInstance(instance)
                            || instance.javaClass == java.lang.Long::class.java && instClass == java.lang.Long.TYPE) {
                            return instance
                        }
                        reportMappingException("DeserializationProblemHandler.handleUnexpectedToken() for type %s returned value of type %s",
                                               instClass, instance.javaClass)
                    }
                    h = h.next()
                }
                if (msg == null) {
                    if (t == null) {
                        msg = String.format("Unexpected end-of-input when binding data into %s",
                                            _calcName(instClass))
                    } else {
                        msg = String.format("Can not deserialize instance of %s out of %s token",
                                            _calcName(instClass), t)
                    }
                }
                reportMappingException(msg)
                return null // never gets here
            }
        }

        val blueprint = FuckingContext(BeanDeserializerFactory.instance)
        val context = blueprint.createInstance(cfg, p, _injectableValues)
        return context.with(context.factory.withAdditionalDeserializers(object:SimpleDeserializers() {
            override fun findEnumDeserializer(type: Class<*>?, config: DeserializationConfig?, beanDesc: BeanDescription?): JsonDeserializer<*> {
                val resolver = EnumResolver.constructUnsafe(type, context.config.annotationIntrospector)
                return object:EnumDeserializer(resolver) {
                    override fun _deserializeOther(p: JsonParser, ctxt: DeserializationContext?): Any {
                        check(p.currentToken == JsonToken.START_OBJECT)
                        run { // Ex: "$$$enum": "aps.RedisLogMessage$SQL$Stage"
                            val fieldName = p.nextFieldName() ?: wtf()
                            check(fieldName == "\$\$\$enum")
                            p.nextTextValue() ?: wtf()
                        }
                        run { // Ex: "value": "SUCCESS"
                            val fieldName = p.nextFieldName() ?: wtf()
                            check(fieldName == "value")
                            val name = p.nextTextValue() ?: wtf()
                            val value = _lookupByName.find(name)
                            check(p.nextToken() == JsonToken.END_OBJECT)
                            return value
                        }
                    }
                }
            }
        }))
    }

    init {
        this.serializerFactory = object:BeanSerializerFactory(null) {
            override fun findBeanProperties(prov: SerializerProvider,
                                            beanDesc: BeanDescription,
                                            builder: BeanSerializerBuilder): List<BeanPropertyWriter>? {

                open class DumbBeanPropertyWriter : BeanPropertyWriter() {
                    override fun fixAccess(config: SerializationConfig?) {
                        // At least not NPE
                    }

                    override fun getSerializationType(): JavaType {
                        // At least not NPE
                        return TypeFactory.unknownType()
                    }
                }

                val writers = super.findBeanProperties(prov, beanDesc, builder) ?: mutableListOf<BeanPropertyWriter>()

                writers.forEachIndexed {i, w ->
                    if (w.type.isEnumType) {
                        writers[i] = object:DumbBeanPropertyWriter() {
                            override fun serializeAsField(bean: Any, gen: JsonGenerator, prov: SerializerProvider?) {
                                gen.writeFieldName(w.name)
                                gen.writeStartObject()
                                gen.writeStringField("\$\$\$enum", w.type.rawClass.name)
                                gen.writeStringField("value", bean.javaClass.getMethod("get${w.name.capitalize()}").invoke(bean)?.toString())
                                gen.writeEndObject()
                            }
                        }
                    }
                    else if (w.type.rawClass == java.lang.Long.TYPE || w.type.rawClass == java.lang.Long::class.java) {
                        writers[i] = object:DumbBeanPropertyWriter() {
                            override fun serializeAsField(bean: Any, gen: JsonGenerator, prov: SerializerProvider?) {
                                gen.writeFieldName(w.name)
                                gen.writeStartObject()
                                gen.writeStringField("\$\$\$primitiveish", "long")
                                gen.writeStringField("value", bean.javaClass.getMethod("get${w.name.capitalize()}").invoke(bean)?.toString())
                                gen.writeEndObject()
                            }
                        }
                    }
                }

                writers.add(0, object:DumbBeanPropertyWriter() {
                    override fun serializeAsField(bean: Any, gen: JsonGenerator, prov: SerializerProvider) {
                        gen.writeStringField("\$\$\$class", bean.javaClass.name)
                    }
                })

                return writers
            }
        }

        this.addHandler(object:DeserializationProblemHandler() {
            override fun handleUnknownProperty(ctxt: DeserializationContext?, p: JsonParser?, deserializer: JsonDeserializer<*>?, beanOrClass: Any?, propertyName: String?): Boolean {
                return propertyName == "\$\$\$class"
            }

            override fun handleUnexpectedToken(ctxt: DeserializationContext?, targetType: Class<*>?, t: JsonToken, p: JsonParser, failureMsg: String?): Any {
                if (targetType == java.lang.Long.TYPE || targetType == java.lang.Long::class.java) {
                    check(p.currentToken == JsonToken.START_OBJECT)
                    run { // Ex: "$$$primitiveish": "long"
                        val fieldName = p.nextFieldName() ?: wtf()
                        check(fieldName == "\$\$\$primitiveish")
                        p.nextTextValue() ?: wtf()
                    }
                    run { // Ex: "value": "9223372036854775807"
                        val fieldName = p.nextFieldName() ?: wtf()
                        check(fieldName == "value")
                        val stringValue = p.nextTextValue() ?: wtf()
                        check(p.nextToken() == JsonToken.END_OBJECT)
                        return stringValue.toLong()
                    }
                }

                return super.handleUnexpectedToken(ctxt, targetType, t, p, failureMsg)
            }
        })
    }
}

val hackyObjectMapper = ObjectMapper().applet {om ->
    om.serializerFactory = object:BeanSerializerFactory(null) {
        override fun findBeanProperties(prov: SerializerProvider,
                                        beanDesc: BeanDescription,
                                        builder: BeanSerializerBuilder): List<BeanPropertyWriter>? {

            open class DumbBeanPropertyWriter : BeanPropertyWriter() {
                override fun fixAccess(config: SerializationConfig?) {
                    // At least not NPE
                }

                override fun getSerializationType(): JavaType {
                    // At least not NPE
                    return TypeFactory.unknownType()
                }
            }

            val writers = super.findBeanProperties(prov, beanDesc, builder) ?: mutableListOf<BeanPropertyWriter>()

            writers.forEachIndexed {i, w ->
                if (w.type.isEnumType) {
                    writers[i] = object:DumbBeanPropertyWriter() {
                        override fun serializeAsField(bean: Any, gen: JsonGenerator, prov: SerializerProvider?) {
                            gen.writeFieldName(w.name)
                            gen.writeStartObject()
                            gen.writeStringField("\$\$\$enum", w.type.rawClass.name)
                            gen.writeStringField("value", bean.javaClass.getMethod("get${w.name.capitalize()}").invoke(bean)?.toString())
                            gen.writeEndObject()
                        }
                    }
                }
            }

            writers.add(0, object:DumbBeanPropertyWriter() {
                override fun serializeAsField(bean: Any, gen: JsonGenerator, prov: SerializerProvider) {
                    gen.writeStringField("\$\$\$class", bean.javaClass.name)
                }
            })

            return writers
        }
    }
}


