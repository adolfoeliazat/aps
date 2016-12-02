package aps.front

class Menu(val items: List<MenuItem>)

class MenuItem(val title: String, val act: () -> Unit)

