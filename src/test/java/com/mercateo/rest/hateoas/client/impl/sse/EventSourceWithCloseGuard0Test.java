package com.mercateo.rest.hateoas.client.impl.sse;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Timer;
import java.util.TimerTask;

import org.glassfish.jersey.media.sse.EventSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

	@Test
	public void testErrorClosing() throws InterruptedException {
		uut = new EventSourceWithCloseGuard(false, eventSource, 1, sseListener, timer);

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
		uut = new EventSourceWithCloseGuard(false, eventSource, 1, sseListener, timer);

		when(eventSource.isOpen()).thenReturn(true);
		uut.close();
		verify(timer).cancel();
		verify(eventSource).close();
	}

	@Test
	public void testClose_syncing() throws InterruptedException {
		uut = new EventSourceWithCloseGuard(false, eventSource, 1000, sseListener, timer);
		when(eventSource.isOpen()).thenReturn(true);
		uut.open();
		ArgumentCaptor<TimerTask> argCaptor = ArgumentCaptor.forClass(TimerTask.class);
		verify(timer).schedule(argCaptor.capture(), anyLong(), anyLong());

		uut.close();

		verify(timer).cancel();
		verify(eventSource).close();

		verify(sseListener, times(0)).onConnectionError();
		verify(eventSource, times(0)).isOpen();

		// simulating the failed schedule of a timer
		argCaptor.getValue().run();
		verify(sseListener, times(0)).onConnectionError();
		verify(eventSource, times(1)).isOpen();
	}

}
