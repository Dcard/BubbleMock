package tw.dcard.bubblemock.module

import tw.dcard.bubblemock.model.MockRequest

/**
 * @author Batu
 */
interface MockSource {
    fun create(): List<MockRequest>
}