package sub.ent.backend;

import static org.junit.Assert.*;

import org.junit.Test;

public class CoreSwapperTest {

	// @Test
	public void test() throws Exception {
		SolrAccess swapper = new SolrAccess();
		swapper.initialize("http://localhost:8983/solr", "fwboffline");
		swapper.switchToCore("fwb");
	}

	// @Test
	public void testStatus() throws Exception {
		SolrAccess swapper = new SolrAccess();
		swapper.initialize("http://localhost:8983/solr", "fwboffline");
		System.out.println(swapper.getCoreDate());
		swapper.initialize("http://localhost:8983/solr", "fwb");
		System.out.println(swapper.getCoreDate());
	}

}
