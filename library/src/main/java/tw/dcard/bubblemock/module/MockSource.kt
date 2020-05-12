package tw.dcard.bubblemock.module

import tw.dcard.bubblemock.model.MockScenario

/**
 * @author Batu
 */
interface MockSource {
    fun create(): List<MockScenario>
}