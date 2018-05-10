package com.nebulights.coinstacks.Portfolio.Intro

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.nebulights.coinstacks.Constants
import com.nebulights.coinstacks.R

/**
 * Created by babramovitch on 4/11/2018.
 * privacy <div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 * key monitor  <div>Icons made by <a href="https://www.flaticon.com/authors/those-icons" title="Those Icons">Those Icons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 * monitor <div>Icons made by <a href="https://www.flaticon.com/authors/roundicons" title="Roundicons">Roundicons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 * manual  <div>Icons made by <a href="https://www.flaticon.com/authors/gregor-cresnar" title="Gregor Cresnar">Gregor Cresnar</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 * exchange <div>Icons made by <a href="https://www.flaticon.com/authors/smashicons" title="Smashicons">Smashicons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 * coin stack <div>Icons made by <a href="https://www.flaticon.com/authors/those-icons" title="Those Icons">Those Icons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 * coins squarer <div>Icons made by <a href="https://www.flaticon.com/authors/those-icons" title="Those Icons">Those Icons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
 */
class IntroActivity : AppIntro() {

    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showStatusBar(false)

        val sliderPage0 = SliderPage()
        sliderPage0.title = "Welcome to CoinStacks"
        sliderPage0.description = "Want to lean more about Coin Stacks?\n\nCheck out the brief guide by swiping left!"
        sliderPage0.imageDrawable = R.drawable.coins
        sliderPage0.bgColor = ContextCompat.getColor(this, R.color.colorPrimary)
        addSlide(AppIntroFragment.newInstance(sliderPage0))

        val sliderPage1 = SliderPage()
        sliderPage1.title = "Privacy First"
        sliderPage1.description = "Coin Stacks does not, and will never collect any crypto related information. To ensure a safe and transparent environment, Coin Stacks is open source\n\nAnonymous crash reports and very basic usage stats like 'Daily Active Users' are collected."
        sliderPage1.imageDrawable = R.drawable.private_sign
        sliderPage1.bgColor = ContextCompat.getColor(this, R.color.colorAccent)
        addSlide(AppIntroFragment.newInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = "Watch An Address"
        sliderPage2.description = "Add a coins public address and it's quantity and value will automatically update using public block explorers."
        sliderPage2.imageDrawable = R.drawable.monitor
        sliderPage2.bgColor = ContextCompat.getColor(this, R.color.colorPrimary)
        addSlide(AppIntroFragment.newInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.title = "Exchange Balances"
        sliderPage3.description = "Track balances from your exchange using API keys. These keys are only ever transmitted from this device to the exchange directly."
        sliderPage3.imageDrawable = R.drawable.exchange
        sliderPage3.bgColor = ContextCompat.getColor(this, R.color.colorAccent)
        addSlide(AppIntroFragment.newInstance(sliderPage3))

        val sliderPage4 = SliderPage()
        sliderPage4.title = "Manual Entry"
        sliderPage4.description = "Don't want to add addresses or exchange keys? You can simply enter how many coins you own."
        sliderPage4.imageDrawable = R.drawable.keyboard
        sliderPage4.bgColor = ContextCompat.getColor(this, R.color.colorPrimary)
        addSlide(AppIntroFragment.newInstance(sliderPage4))

        setSeparatorColor(Color.TRANSPARENT)

        showSkipButton(true)
        isProgressButtonEnabled = true
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putBoolean(Constants.FIRST_LOAD_KEY, false).apply()
        finish()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putBoolean(Constants.FIRST_LOAD_KEY, false).apply()
        finish()
    }

    override fun onPageSelected(position: Int) {
        currentPage = position
        super.onPageSelected(position)
    }

    override fun onBackPressed() {
        if (currentPage == 0) {
            moveTaskToBack(true)
            return
        }
        super.onBackPressed()
    }
}