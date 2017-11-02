package com.mercateo.rest.hateoas.client.impl.sse;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Timer;

import org.glassfish.jersey.media.sse.EventSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventSourceWithCloseGuard0Test {
	@Mock
	private EventSource eventSource;

	@Mock
	private SSEListener<?> sseListener;

	@Spy
	private Timer timer = new Timer(true);

	private EventSourceWithCloseGuard uut;

	@Before
	public void setup() {
		uut = new EventSourceWithCloseGuard(eventSource, 1, sseListener, timer);
	}

	@Test
	public void testErrorClosing() throws InterruptedException {
		when(eventSource.isOpen()).thenReturn(true);
		uut.open();
		verify(timer).schedule(any(), anyLong(), anyLong());
		verify(eventSource).open();

		when(eventSource.isOpen()).thenReturn(false);
		Thread.sleep(20);
		verify(timer).cancel();
		verify(sseListener).onConnectionError();
	}

	@Test
	public void testClose() throws InterruptedException {
		when(eventSource.isOpen()).thenReturn(true);
		uut.close();
		verify(timer).cancel();
		verify(eventSource).close();
	}

}
