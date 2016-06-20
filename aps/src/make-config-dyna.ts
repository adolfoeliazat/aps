import * as webdriverio from 'webdriverio'
import static 'into-u'

let wio

export async function onKey(key, {buildStaticSites}) {
    if (key === '0') {
        if (wio) {
            wio.end()
            wio = undefined
        }
        
        await buildStaticSites()
        
//        const url = 'http://127.0.0.1:3012'
//        const url = 'http://127.0.0.1:3012/prices.html'
        const url = 'http://127.0.0.1:3012/why.html'
        doNoisa(async function() {
            wio = webdriverio.remote({
                desiredCapabilities: {
                    browserName: 'chrome'
                }
            })
            await wio.init()
            await wio.windowHandleMaximize()
            await wio.url(url)
            // await wio.scroll(0, 500)
            await openDevConsole()
        })
    }
}

export function dispose() {
    // clog('Disposing dynamic shit')
    if (wio) {
        wio.end()
    }
}

async function openDevConsole() {
    await wio.keys('\uE009\uE008j\uE000')
}

async function pressDown(times=1) {
    await wio.keys(repeat('\uE015', times))
}

/*
SELENIUM KEYS
-------------

Key     Code    Type
NULL    \uE000  NULL
CANCEL  \uE001  Special key
HELP    \uE002  Special key
BACK_SPACE  \uE003  Special key
TAB \uE004  Special key
CLEAR   \uE005  Special key
RETURN  \uE006  Special key
ENTER   \uE007  Special key
SHIFT   \uE008  Modifier
CONTROL \uE009  Modifier
ALT \uE00A  Modifier
PAUSE   \uE00B  Special key
ESCAPE  \uE00C  Special key
SPACE   \uE00D  Special key
PAGE_UP \uE00E  Special key
PAGE_DOWN   \uE00F  Special key
END \uE010  Special key
HOME    \uE011  Special key
ARROW_LEFT  \uE012  Special key
ARROW_UP    \uE013  Special key
ARROW_RIGHT \uE014  Special key
ARROW_DOWN  \uE015  Special key
INSERT  \uE016  Special key
DELETE  \uE017  Special key
SEMICOLON   \uE018  Special key
EQUALS  \uE019  Special key
NUMPAD0 \uE01A  Special key
NUMPAD1 \uE01B  Special key
NUMPAD2 \uE01C  Special key
NUMPAD3 \uE01D  Special key
NUMPAD4 \uE01E  Special key
NUMPAD5 \uE01F  Special key
NUMPAD6 \uE020  Special key
NUMPAD7 \uE021  Special key
NUMPAD8 \uE022  Special key
NUMPAD9 \uE023  Special key
MULTIPLY    \uE024  Special key
ADD \uE025  Special key
SEPARATOR   \uE026  Special key
SUBTRACT    \uE027  Special key
DECIMAL \uE028  Special key
DIVIDE  \uE029  Special key
F1  \uE031  Special key
F2  \uE032  Special key
F3  \uE033  Special key
F4  \uE034  Special key
F5  \uE035  Special key
F6  \uE036  Special key
F7  \uE037  Special key
F8  \uE038  Special key
F9  \uE039  Special key
F10 \uE03A  Special key
F11 \uE03B  Special key
F12 \uE03C  Special key
META    \uE03D  Special key
COMMAND \uE03D  Special key
ZENKAKU_HANKAKU \uE040  Special key
*/
