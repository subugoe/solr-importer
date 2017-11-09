package sub.ent.backend;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.Test;

import sub.ent.backend.Timer;

public class TimerTest {

	@Test
	public void test() {
		Timer timer = new Timer();
		timer.setStart(1);
		timer.setStop(100001);
		System.out.println(timer.getDurationMessage());
		assertThat(timer.getDurationMessage(), containsString("1:40 minutes"));
	}

}
