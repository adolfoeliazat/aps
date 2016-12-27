console.log('pizda')
chrome.tabs.captureVisibleTab({format: 'png'}, dataUrl => {
    console.log(dataUrl.slice(0, 100))
})

