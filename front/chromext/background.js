chrome.runtime.onConnect.addListener(port => {
    port.onMessage.addListener(msg => {
        console.log("Holy fuck, I've got a message from content script", msg)

        // Ex: window.postMessage({fun: 'captureShit'}, '*')
        if (msg.fun === 'captureShit') {
            chrome.tabs.captureVisibleTab({format: 'png'}, dataURL => {
                console.log('A piece of captured shit', dataURL.slice(0, 100))
                port.postMessage({fun: 'shitCaptured', dataURL})
            })
        }
    })
})

