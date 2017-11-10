package sub.ent.web;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sub.ent.backend.Importer;
import sub.ent.backend.ImporterFactory;


@Controller
public class MainController {


	@RequestMapping(method = RequestMethod.GET, value = "/test3")
	@ResponseBody
	public ResponseEntity<?> getTest() throws IOException {
		ImporterFactory factory = new ImporterFactory();
		Importer importer = factory.getImporter();
		importer.executeAllSteps();
		return ResponseEntity.ok().body("blub");
	}

}
