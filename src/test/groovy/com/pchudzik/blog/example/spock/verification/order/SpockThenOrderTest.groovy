package com.pchudzik.blog.example.spock.verification.order

import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SpockThenOrderTest extends Specification {
	final Executor executor = Executors.newFixedThreadPool(4)

	@Unroll
	def "no order failure"() {
		given:
		final notifier = Mock(Notifier)
		CountDownLatch latch = new CountDownLatch(1)

		when:
		executor.execute({ ->
			notifier.notifyCompletion()
			latch.countDown()
		})

		then:
		latch.await(2, TimeUnit.SECONDS)
		1 * notifier.notifyCompletion()

		where:
		i << (1..10)
	}

	@Unroll
	def "fixed order pass"() {
		given:
		final notifier = Mock(Notifier)
		final latch = new CountDownLatch(1)

		when:
		executor.execute({ ->
			notifier.notifyCompletion()
			latch.countDown()
		})

		then:
		latch.await(2, TimeUnit.SECONDS)

		then:
		1 * notifier.notifyCompletion()

		where:
		i << (1..10)
	}

	interface Notifier {
		void notifyCompletion();
	}
}
