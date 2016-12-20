package aps.front

import aps.*

@native object ReactDOMServer {
    fun renderToStaticMarkup(el: ReactElement): String = noImpl
}

