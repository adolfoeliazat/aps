console.log('Injecting APS chromext shit')

const port = chrome.runtime.connect()

window.addEventListener('message', event => {
    if (event.source !== window) return
    port.postMessage(event.data)
}, false)

port.onMessage.addListener(msg => {
    console.log("Holy fuck, I've got a message from background", msg)
})

