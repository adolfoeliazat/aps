package aps.back.spike

import aps.*
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute
import org.apache.lucene.analysis.tokenattributes.TypeAttribute

fun main(args: Array<String>) {
    val analyzer = RussianAnalyzer()
    val tokenStream = analyzer.tokenStream("shit", pieceOfShit)
    tokenStream.reset()
    while (tokenStream.incrementToken()) {
        val typeAttribute = tokenStream.getAttribute(TypeAttribute::class.java)
        val type = typeAttribute.type()
        println("type: " + type)

        val offsetAttribute = tokenStream.getAttribute(OffsetAttribute::class.java)
        val startOffset = offsetAttribute.startOffset()
        val endOffset = offsetAttribute.endOffset()
        println("startOffset: " + startOffset)
        println("endOffset: " + endOffset)

        val charTermAttribute = tokenStream.getAttribute(CharTermAttribute::class.java)
        val text = charTermAttribute.toString()
        println("text: " + text)


//        for (attributeClass in tokenStream.attributeClassesIterator) {
//            println("Attribute class: " + attributeClass)
//            val attribute = tokenStream.getAttribute(attributeClass)
//            println("Attribute: " + attribute)
//        }
        println("------------------------------------------------------------------")
    }

    println("Fuck yeah")
}

private val pieceOfShit = dedent("""
    26-летний князь Лев Николаевич Мышкин возвращается из санатория \ в Швейцарии, \{где\} провёл несколько лет, лечась от эпилепсии. Князь предстаёт человеком искренним и невинным, хотя и прилично разбирающимся в отношениях между людьми. Он едет в Россию к единственным оставшимся у него родственникам — семье Епанчиных. В поезде он знакомится с молодым купцом Парфёном Рогожиным и отставным чиновником Лебедевым, которым бесхитростно рассказывает свою историю. В ответ он узнаёт подробности жизни Рогожина, который влюблён в Настасью Филипповну, бывшую содержанку богатого дворянина Афанасия Ивановича Тоцкого.

    В доме Епанчиных выясняется, что Настасья Филипповна там хорошо известна. Есть план выдать её за протеже генерала Епанчина Гаврилу Ардалионовича Иволгина, человека амбициозного, но посредственного. Князь Мышкин знакомится со всеми основными персонажами повествования. Это дочери Епанчиных — Александра, Аделаида и Аглая, на которых он производит благоприятное впечатление, оставаясь объектом их немного насмешливого внимания. Это генеральша Лизавета Прокофьевна Епанчина, которая находится в постоянном волнении из-за того, что её муж общается с Настасьей Филипповной, имеющей репутацию падшей. Это Ганя Иволгин, который очень страдает из-за предстоящей ему роли мужа Настасьи Филипповны, хотя ради денег готов на всё, и не может решиться на развитие своих пока очень слабых отношений с Аглаей. Князь Мышкин довольно простодушно рассказывает генеральше и сёстрам Епанчиным о том, что он узнал о Настасье Филипповне от Рогожина, а также поражает их своим повествованием о воспоминаниях и чувствах своего знакомого, который был приговорён к смертной казни, но в последний момент был помилован.
""")

