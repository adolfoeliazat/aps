package aps.back

import aps.*
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute
import org.apache.lucene.analysis.tokenattributes.TypeAttribute
import kotlin.properties.Delegates.notNull

val russianAnalyzer by lazy {RussianAnalyzer()}

fun luceneHighlightRanges(text: String, searchWords: List<String>, analyzer: RussianAnalyzer): List<IntRangeRTO> {
    val searchWordsJoined = searchWords.joinToString(" ")
    val usefulStemmedSearchWords = luceneParse(searchWordsJoined, analyzer).map {it.text}.toSet()
    val tokens = luceneParse(text, analyzer)
    val highlightRanges = tokens
        .filter {usefulStemmedSearchWords.contains(it.text)}
        .map {IntRangeRTO(it.startOffset, it.endOffset - 1)}
    return highlightRanges
}

fun luceneParse(text: String, analyzer: Analyzer): List<LuceneParseToken> {
    return mutableListOf<LuceneParseToken>()-{res->
        analyzer.tokenStream("shit", text).use {stream->
            stream.reset()
            while (stream.incrementToken()) {
                res.add(LuceneParseToken()-{o->
                    val typeAttribute = stream.getAttribute(TypeAttribute::class.java)
                    o.type = typeAttribute.type()

                    val offsetAttribute = stream.getAttribute(OffsetAttribute::class.java)
                    o.startOffset = offsetAttribute.startOffset()
                    o.endOffset = offsetAttribute.endOffset()

                    val charTermAttribute = stream.getAttribute(CharTermAttribute::class.java)
                    o.text = charTermAttribute.toString()
                })
            }
        }
    }
}

