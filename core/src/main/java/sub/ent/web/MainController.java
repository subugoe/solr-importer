package sub.ent.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class MainController {


	@RequestMapping(method = RequestMethod.GET, value = "/test3")
	@ResponseBody
	public ResponseEntity<?> getTest() {
		return ResponseEntity.ok().body("some test 3");
	}

}
