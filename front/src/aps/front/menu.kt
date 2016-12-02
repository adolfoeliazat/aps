package aps.front

import aps.*

class Menu(val items: List<MenuItem>)

class MenuItem(val title: String, val act: () -> Promise<Unit>)

