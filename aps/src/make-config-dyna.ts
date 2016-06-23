import static 'into-u'

export async function onKey(key, {buildStaticSites}) {
    if (key === '0') {
        await buildStaticSites()
    }
}

export async function dispose() {
}
