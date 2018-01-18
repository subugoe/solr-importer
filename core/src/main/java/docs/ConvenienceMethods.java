package docs;

import static org.mockito.Mockito.*;

public class ConvenienceMethods {

	public static <T> T objectOf(Class<T> clazz) {
		return mock(clazz);
	}

	public static <T> T arg(Class<T> clazz) {
		return mock(clazz);
	}

	public static <T> T class_(Class<T> clazz) {
		return mock(clazz);
	}
}
