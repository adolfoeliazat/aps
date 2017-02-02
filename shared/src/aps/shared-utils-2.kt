package aps

fun escapeHTML(s: String) =
    s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


