package docs;

import static org.mockito.Mockito.*;

class ConvenienceMethods {

	static <T> T objectOf(Class<T> clazz) {
		return mock(clazz);
	}

	static <T> T arg(Class<T> clazz) {
		return mock(clazz);
	}

	static <T> T the(Class<T> clazz) {
		return mock(clazz);
	}

	static <T> T documentationIn(Class<T> clazz) {
		return mock(clazz);
	}

}
