package com.example.tfg.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.tfg.domain.Degree;
import com.example.tfg.service.DegreeService;

@Controller
public class DegreeController {

	@Autowired
	private DegreeService serviceDegree;

	@RequestMapping(value = "/degree/add.htm", method = RequestMethod.GET)
	protected String getAddNewDegreeForm(Model model) {
		Degree newDegree = new Degree();
		// newDegree.setCode(serviceDegree.getNextCode());

		model.addAttribute("addDegree", newDegree);
		return "degree/add";
	}

	@RequestMapping(value = "/degree/add.htm", method = RequestMethod.POST)
	// Every Post have to return redirect
	public String processAddNewDegree(
			@ModelAttribute("addDegree") Degree newDegree) {
		boolean created = serviceDegree.addDegree(newDegree);
		if (created)
			return "redirect:/degree/list.htm";
		else
			return "redirect:/degree/add.htm";
	}

	/**
	 * Methods for listing degrees
	 */

	@RequestMapping(value = "/degree/list.htm")
	public ModelAndView handleRequestDegreeList(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Map<String, Object> myModel = new HashMap<String, Object>();

		List<Degree> result = serviceDegree.getAll();
		myModel.put("degrees", result);

		return new ModelAndView("degree/list", "model", myModel);
	}

	/**
	 * Methods for modify degrees
	 */
	@RequestMapping(value = "/degree/{degreeId}/modify.htm", method = RequestMethod.POST)
	public String formModifyDegree(@PathVariable("degreeId") Long id,
			@ModelAttribute("modifyDegree") Degree modify)

	{
		// modify.setId(id);
		boolean modified = serviceDegree.modifyDegree(modify, id);
		if (modified)
			return "redirect:/degree/list.htm";
		else
			return "redirect:/error.htm";
	}

	@RequestMapping(value = "/degree/{degreeId}/modify.htm", method = RequestMethod.GET)
	protected ModelAndView formModifyDegrees(@PathVariable("degreeId") long id)
			throws ServletException {
		ModelAndView model = new ModelAndView();
		Degree p = serviceDegree.getDegree(id);
		model.addObject("modifyDegree", p);
		model.setViewName("degree/modify");

		return model;
	}

	/**
	 * Methods for delete degrees
	 */

	@RequestMapping(value = "/degree/delete/{degreeId}.htm", method = RequestMethod.GET)
	public String formDeleteDegrees(@PathVariable("degreeId") long id)
			throws ServletException {

		if (serviceDegree.deleteDegree(id)) {
			return "redirect:/degree/list.htm";
		} else
			return "redirect:/error.htm";
	}

	/**
	 * Methods for view degrees
	 */
	@RequestMapping(value = "/degree/{degreeId}.htm", method = RequestMethod.GET)
	protected ModelAndView formViewDegree(@PathVariable("degreeId") Long id)
			throws ServletException {

		Map<String, Object> myModel = new HashMap<String, Object>();

		// Degree p = serviceDegree.getDegree(id);

		Degree p = serviceDegree.getDegreeAll(id);

		myModel.put("degree", p);
		if (p.getSubjects() != null)
			myModel.put("subjects", p.getSubjects());
		if (p.getCompetences() != null)
			myModel.put("competences", p.getCompetences());

		return new ModelAndView("degree/view", "model", myModel);
	}

}
