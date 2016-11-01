var testimonials

initStaticShit()


function initStaticShit() {
    testimonials = Testimonials()
}

function disposeStaticShit() {
    testimonials.dispose()
}

function Testimonials() {
    var numSlides = 3,
    numFrames = 60,
    autoIgnitionPeriod = 15000,
    rightArrowAppearsDelay = 200,
    shiftFractions,
    slide,
    autoIgnitionTimeoutHandle,
    magic,
    disposed

    if (!$('#testimonials-window').length) {
        return {
            dispose() {}
        }
    }

    window.testimonialsIncarnationID = window.testimonialsIncarnationID || 0
    ++window.testimonialsIncarnationID

    calcTrajectory()

    $('.testimonials-item').slice(0, 2).clone().appendTo($('#testimonials-strip'))
    $('#testimonials-right').on('click', ignite)

    var mqLarge = window.matchMedia('(min-width: 1200px)')
    var mqMedium = window.matchMedia('(min-width: 992px)')
    mqLarge.addListener(onMediaChange)
    mqMedium.addListener(onMediaChange)

    onMediaChange()

    return {
        dispose() {
            mqLarge.removeListener(onMediaChange)
            mqMedium.removeListener(onMediaChange)
            disposed = true
        }
    }


    function calcTrajectory() {
        var plot = [
        'o                             ',
        '  o                           ',
        '       o                      ',
        '                   o          ',
        '               o              ',
        '                  o           ',
        '                o             ',
        '                 o            ',
        '------------------------------',
        '                 x            ']

        var numShiftFractions,  posFor100
        for (var i = 0; i < plot.length; ++i) {
        if (plot[i].charAt(0) === '-') {
            numShiftFractions = i
            posFor100 = plot[i + 1].indexOf('x')
        }
    }

        shiftFractions = []
        for (var i = 0; i < numShiftFractions; ++i) {
        var pos = plot[i].indexOf('o')
        shiftFractions.push(pos / posFor100)
    }
    }

    function onMediaChange() {
        // console.log('Testimonials onMediaChange ' + testimonialsIncarnationID)
        clearTimeout(autoIgnitionTimeoutHandle)
        slide = 0
        $('#testimonials-strip').css('margin-left', '')
        $('.testimonials-item').css('margin-right', '').css('visibility', '')

        magic = undefined
        if (window.matchMedia('(min-width: 1200px)').matches) {
            // console.log('Screen is large')
            magic = 970 + 200
        } else if (window.matchMedia('(min-width: 992px)').matches) {
            // console.log('Screen is medium')
            magic = 970
        }

        if (!magic) {
            console.log('No testimonial sliding for you')
            $('#testimonials-right').hide()
            var items = $('.testimonials-item')
            $(items[items.length - 1]).hide()
            $(items[items.length - 2]).hide()
            return
        }

        var items = $('.testimonials-item')
        $(items[items.length - 1]).show()
        $(items[items.length - 2]).show()
        $('#testimonials-right').show()
        scheduleAutoIgnition()
    }

    function scheduleAutoIgnition() {
        autoIgnitionTimeoutHandle = setTimeout(ignite, autoIgnitionPeriod)
    }

    function ignite() {
        if (disposed) return
        // console.log('Igniting testimonials ' + testimonialsIncarnationID)
        clearTimeout(autoIgnitionTimeoutHandle)
        $('#testimonials-right').hide()

        var elementToEnlargeRight = $($('.testimonials-item')[slide * 2 + 3])
        elementToEnlargeRight.css('margin-right', '300px')
        var elementToHideOnHitFloor = $($('.testimonials-item')[slide * 2 + 1])

        var framesDone = 0
        requestAnimationFrame(step)

        function step() {
            var maxShift = magic

            var shiftFractionIdxMiddle = (shiftFractions.length - 1) * framesDone / (numFrames - 1)
            var shiftFractionIdx1 = Math.floor(shiftFractionIdxMiddle)
            var shiftFractionIdx2 = Math.ceil(shiftFractionIdxMiddle)
            var shiftFractionFraction = shiftFractionIdxMiddle - shiftFractionIdx1
            var shiftFraction1 = shiftFractions[shiftFractionIdx1]
            var shiftFraction2 = shiftFractions[shiftFractionIdx2]
            var shiftFraction = shiftFraction1 + (shiftFraction2 - shiftFraction1) * shiftFractionFraction
            var shift = maxShift * shiftFraction
            var offset = slide * magic + shift

            $('#testimonials-strip').css('margin-left', -offset + 'px')
            if (shift >= maxShift) {
                elementToHideOnHitFloor.css('visibility', 'hidden')
            }

            if (++framesDone === numFrames) {
                if (++slide === numSlides) {
                    slide = 0
                }

                elementToEnlargeRight.css('margin-right', '')
                elementToHideOnHitFloor.css('visibility', '')

                setTimeout(function() {
                    if (disposed) return
                    $('#testimonials-right').show()
                    scheduleAutoIgnition()
                }, rightArrowAppearsDelay)
            } else {
                requestAnimationFrame(step)
            }
        }
    }
}

