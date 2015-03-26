package es.ucm.fdi.dalgs.subject.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.ucm.fdi.dalgs.classes.ResultClass;
import es.ucm.fdi.dalgs.competence.service.CompetenceService;
import es.ucm.fdi.dalgs.domain.Competence;
import es.ucm.fdi.dalgs.domain.Subject;
import es.ucm.fdi.dalgs.subject.service.SubjectService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SubjectController {

	@Autowired
	private SubjectService serviceSubject;

	@Autowired
	private CompetenceService serviceCompetence;



	private static final Logger logger = LoggerFactory
			.getLogger(SubjectController.class);

	/**
	 * Methods for delete subjects
	 */

	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/{subjectId}/delete.htm", method = RequestMethod.GET)
	public String deleteSubjectGET(
			@PathVariable("degreeId") Long id_degree,
			@PathVariable("moduleId") Long id_module,
			@PathVariable("topicId") Long id_topic,
			@PathVariable("subjectId") Long id_subject) throws ServletException {

		if (serviceSubject.deleteSubject(serviceSubject.getSubject(id_subject).getSingleElement()).getSingleElement()) {
			return "redirect:/degree/" + id_degree + "/module/"+ id_module + "/topic/" + id_topic + ".htm";
		} else
			return "redirect:/error.htm";
	}

	/**
	 * Methods for adding subjects
	 */
	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/add.htm", method = RequestMethod.GET)
	protected String addSubjectGET(Model model,
			@PathVariable("degreeId") Long id_degree) {
		
		if(!model.containsAttribute("subject"))
			model.addAttribute("subject", new Subject());
		model.addAttribute("valueButton", "Add");
		model.addAttribute("typeform", "form.add");

		return "subject/form";
	}

	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/add", method = RequestMethod.POST, params="Add")
	// Every Post have to return redirect
	public String addSubjectPOST(
			@ModelAttribute("subject") Subject newSubject,
			@PathVariable("degreeId") Long id_degree,
			@PathVariable("moduleId") Long id_module,
			@PathVariable("topicId") Long id_topic,
			BindingResult resultBinding, RedirectAttributes attr, Locale locale) {

		if (!resultBinding.hasErrors()){

			ResultClass<Subject> result = serviceSubject.addSubject(newSubject, id_topic, locale);
			if (!result.hasErrors())
				return "redirect:/degree/" + id_degree + "/module/"+ id_module + "/topic/" + id_topic + ".htm";
			else{

				if (result.isElementDeleted()){
					attr.addFlashAttribute("unDelete", result.isElementDeleted()); 
					attr.addFlashAttribute("subject", result.getSingleElement());

				}else attr.addFlashAttribute("subject", newSubject);
				attr.addFlashAttribute("errors", result.getErrorsList());


			}
		}else{
			attr.addFlashAttribute("subject", newSubject);
			attr.addFlashAttribute(
					"org.springframework.validation.BindingResult.subject",
					resultBinding);			
		}
		return "redirect:/degree/"+ id_degree+"/module/"+ id_module+"/topic/"+id_topic+"/subject/add.htm";
	}


	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/add", method = RequestMethod.POST, params="Undelete")
	// Every Post have to return redirect
	public String undeleteSubject(
			@ModelAttribute("subject") Subject subject, 
			@PathVariable("degreeId") Long id_degree,
			@PathVariable("moduleId") Long id_module,
			@PathVariable("topicId") Long id_topic,
			BindingResult resultBinding, RedirectAttributes attr, Locale locale) {

		if (!resultBinding.hasErrors()){
			ResultClass<Subject> result = serviceSubject.unDeleteSubject(subject, locale);

			if (!result.hasErrors()){
				attr.addFlashAttribute("subject", result.getSingleElement());
				return "redirect:/degree/" + id_degree + "/module/"+ id_module + "/topic/" + id_topic + "/subject/"
						+result.getSingleElement().getId()+"/modify.htm";
			}else{

				if (result.isElementDeleted())
					attr.addFlashAttribute("unDelete", true); 
				attr.addFlashAttribute("errors", result.getErrorsList());

			}
		}else{
			attr.addFlashAttribute(
					"org.springframework.validation.BindingResult.topic",
					resultBinding);
		}
		attr.addFlashAttribute("subject", subject);
		return "/degree/"+ id_degree+"/module/"+ id_module+"/topic/"+id_topic+"/subject/add.htm";
	}
	/**
	 * Methods for modify subjects
	 */
	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/{subjectId}/modify.htm", method = RequestMethod.POST)
	public String modifySubjectPOST(
			@PathVariable("degreeId") Long id_degree,
			@PathVariable("moduleId") Long id_module,
			@PathVariable("topicId") Long id_topic,
			@PathVariable("subjectId") Long id_subject,
			@ModelAttribute("modifySubject") Subject modify,
			BindingResult resultBinding, RedirectAttributes attr, Locale locale)

	{
		if (!resultBinding.hasErrors()){	

			ResultClass<Boolean> result = serviceSubject.modifySubject(modify, id_subject, locale);
			if (!result.hasErrors())
				return "redirect:/degree/" + id_degree + "/module/"+ id_module + "/topic/" + id_topic + ".htm";
			else
				attr.addFlashAttribute("errors", result.getErrorsList());
		}
		
		else{
			attr.addFlashAttribute(
					"org.springframework.validation.BindingResult.topic",
					resultBinding);
		}
		attr.addFlashAttribute("subject", modify);
		return "/degree/"+ id_degree+"/module/"+ id_module+"/topic/"+id_topic+"/subject/"+id_subject+"/modify.htm";



	}

	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/{subjectId}/modify.htm", method = RequestMethod.GET)
	protected String modifySubjectGET(
			@PathVariable("degreeId") Long id_degree,
			@PathVariable("moduleId") Long id_module,
			@PathVariable("topicId") Long id_topic,
			@PathVariable("subjectId") Long id_subject,
			Model model) throws ServletException {

		if (!model.containsAttribute("subject")){
			Subject p = serviceSubject.getSubject(id_subject).getSingleElement();
			model.addAttribute("subject", p);
		}

		model.addAttribute("valueButton", "Modify");
		model.addAttribute("typeform", "form.modify");
		return "subject/form";
	}

	/**
	 * Methods for view subjects
	 */
	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/{subjectId}.htm", method = RequestMethod.GET)
	protected ModelAndView getSubjectGET(
			@PathVariable("degreeId") Long id_degree,
			@PathVariable("subjectId") Long id_subject) throws ServletException {

		Map<String, Object> myModel = new HashMap<String, Object>();

		Subject p = serviceSubject.getSubjectAll(id_subject).getSingleElement();

		myModel.put("subject", p);
		myModel.put("topic", p.getTopic());

		if (p.getCompetences() != null)
			myModel.put("competences", p.getCompetences());

		return new ModelAndView("subject/view", "model", myModel);
	}

	/**
	 * Method for manage competences of a subject
	 */

	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/{subjectId}/competence/{competenceId}/delete.htm", method = RequestMethod.GET)
	public String deleteCompetenceFromSubjectGET(
			@PathVariable("degreeId") Long id_degree,
			@PathVariable("moduleId") Long id_module,
			@PathVariable("moduleId") Long id_topic,
			@PathVariable("subjectId") Long id_subject,
			@PathVariable("competenceId") Long id_competence)
					throws ServletException {

		if (serviceCompetence.deleteCompetenceFromSubject(id_competence,
				id_subject).getSingleElement()) {
			return "redirect:/degree/" + id_degree + "/module/"+ id_module + "/topic/" + id_topic + ".htm";
		} else
			return "redirect:/error.htm";
	}

	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/{subjectId}/addCompetences.htm", method = RequestMethod.GET)
	protected String addCompetenceToSubjectGET(
			@PathVariable("degreeId") Long id_degree,
			@PathVariable("subjectId") Long id_subject, Model model) {

		Subject s = serviceSubject.getSubject(id_subject).getSingleElement();
		Collection<Competence> competences = serviceCompetence
				.getCompetencesForDegree(id_degree, false);

		model.addAttribute("subject", s);
		model.addAttribute("competences", competences);

		return "subject/addCompetences";
	}

	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/{subjectId}/addCompetences.htm", method = RequestMethod.POST)
	// Every Post have to return redirect
	public String addCompetenceToSubjectPOST(
			@PathVariable("degreeId") Long id_degree,
			@PathVariable("moduleId") Long id_module,
			@PathVariable("topicId") Long id_topic,
			@PathVariable("subjectId") Long id_subject,
			@ModelAttribute("subject") Subject subject, BindingResult result,
			Model model) {

		if (!result.hasErrors()){
			try {
				serviceSubject.addCompetences(subject, id_subject);
				return "redirect:/degree/" + id_degree + "/module/"+ id_module + "/topic/" + id_topic + ".htm";
			} catch (Exception e) {
				return "redirect:/competence/add.htm";
			}
		}
		else return "redirect:/error.htm";

	}
	
	@RequestMapping(value = "/degree/{degreeId}/module/{moduleId}/topic/{topicId}/subject/{subjectId}/restore.htm")
	// Every Post have to return redirect
	public String restoreSubject(@PathVariable("degreeId") Long id_degree,
			@PathVariable("moduleId") Long id_module,
			@PathVariable("topicId") Long id_topic,
			@PathVariable("subjectId") Long id_subject, Locale locale) {
		
		ResultClass<Subject> result = serviceSubject.unDeleteSubject(serviceSubject.getSubject(id_subject).getSingleElement(), locale);
		if (!result.hasErrors())
			return "redirect:/degree/"+id_degree+"/module/"+id_module+".htm";
		else{
			return "redirect:/error.htm";

		}

	}
	

	/**
	 * For binding the competences of the subject.
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) throws Exception {
		binder.registerCustomEditor(Set.class, "competences",
				new CustomCollectionEditor(Set.class) {
			protected Object convertElement(Object element) {
				if (element instanceof Competence) {
					logger.info("Converting...{}", element);
					return element;
				}
				if (element instanceof String) {
					Competence competence = serviceCompetence
							.getCompetenceByName(element.toString()).getSingleElement();
					logger.info("Loking up {} to {}", element,
							competence);

					return competence;
				}
				System.out.println("Don't know what to do with: "
						+ element);
				return null;
			}
		});
	}

}
