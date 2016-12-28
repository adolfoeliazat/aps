chrome.runtime.onConnect.addListener(port => {
    port.onMessage.addListener(msg => {
        // console.log("Holy fuck, I've got a message from content script", msg)

        // Ex: window.postMessage({type: 'captureVisualShit'}, '*')
        if (msg.type === 'captureVisualShit') {
            chrome.tabs.captureVisibleTab({format: 'png'}, dataURL => {
                console.log('A piece of captured shit', dataURL.slice(0, 100))
                port.postMessage({type: 'visualShitCaptured', dataURL})
            })
        }
    })
})

