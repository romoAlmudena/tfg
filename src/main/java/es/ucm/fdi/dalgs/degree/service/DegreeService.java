package es.ucm.fdi.dalgs.degree.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ucm.fdi.dalgs.academicTerm.service.AcademicTermService;
import es.ucm.fdi.dalgs.classes.ResultClass;
import es.ucm.fdi.dalgs.competence.service.CompetenceService;
import es.ucm.fdi.dalgs.degree.repository.DegreeRepository;
import es.ucm.fdi.dalgs.domain.AcademicTerm;
import es.ucm.fdi.dalgs.domain.Degree;
import es.ucm.fdi.dalgs.domain.Subject;
import es.ucm.fdi.dalgs.module.service.ModuleService;

@Service
public class DegreeService {
	@Autowired
	private DegreeRepository daoDegree;

	@Autowired
	private ModuleService serviceModule;

	@Autowired
	private CompetenceService serviceCompetence;

	@Autowired
	private AcademicTermService serviceAcademicTerm;

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@Transactional(readOnly=false)
	public ResultClass<Boolean> addDegree(Degree degree) {

		Degree degreeExists = daoDegree.existByCode(degree.getInfo().getCode());
		ResultClass<Boolean> result = new ResultClass<Boolean>();

		if( degreeExists != null){
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<String>();
			errors.add("Code already exists");

			if (degreeExists.getIsDeleted()){
				result.setElementDeleted(true);
				errors.add("Element is deleted");

			}
			result.setErrorsList(errors);
		}
		else{
			boolean r = daoDegree.addDegree(degree);
			if (r) 
				result.setE(true);
		}
		return result;

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public ResultClass<List<Degree>> getAll() {
		ResultClass<List<Degree>> result = new ResultClass<List<Degree>>();
		result.setE(daoDegree.getAll());
		return result;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@Transactional(readOnly = false)
	public ResultClass<Boolean> modifyDegree(Degree degree, Long id_degree) {
		ResultClass<Boolean> result = new ResultClass<Boolean>();

		Degree modifydegree = daoDegree.getDegree(id_degree);
		
		Degree degreeExists = daoDegree.existByCode(degree.getInfo().getCode());
		
		if(!degree.getInfo().getCode().equalsIgnoreCase(modifydegree.getInfo().getCode()) && 
				degreeExists != null){
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<String>();
			errors.add("New code already exists");

			if (degreeExists.getIsDeleted()){
				result.setElementDeleted(true);
				errors.add("Element is deleted");

			}
			result.setErrorsList(errors);
		}
		else{
			modifydegree.setInfo(degree.getInfo());
			boolean r = daoDegree.saveDegree(modifydegree);
			if (r) 
				result.setE(true);
		}
		return result;

		
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = false)
	public ResultClass<Degree> getDegree(Long id) {
		ResultClass<Degree> result = new ResultClass<Degree>();
		result.setE(daoDegree.getDegree(id));
		return result;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@Transactional(readOnly = false)
	public ResultClass<Boolean> deleteDegree(Long id) {
		ResultClass<Boolean> result = new ResultClass<Boolean>();
		boolean deleteModules = false;
		boolean deleteCompetences = false;
		boolean deleteAcademic = false;

		Degree d = daoDegree.getDegree(id);
		if (!d.getModules().isEmpty())
			deleteModules = serviceModule.deleteModulesForDegree(d).getE();
		if (!d.getCompetences().isEmpty())
			deleteCompetences = serviceCompetence.deleteCompetencesForDegree(d).getE();
		Collection<AcademicTerm> academicList = serviceAcademicTerm.getAcademicTermsByDegree(id).getE();


		if(!academicList.isEmpty()) deleteAcademic = serviceAcademicTerm.deleteAcademicTermCollection(academicList).getE();
		if ((deleteModules || d.getModules().isEmpty()) && (deleteCompetences || d.getCompetences().isEmpty())
				&& (deleteAcademic || academicList.isEmpty())){
			result.setE(daoDegree.deleteDegree(d));	
			return result;
		} else{
			result.setE(false);
			return result;
		}
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public ResultClass<Degree> getDegreeSubject(Subject p) {
		ResultClass<Degree> result = new ResultClass<Degree>();
		result.setE(daoDegree.getDegreeSubject(p));
		return result;
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public ResultClass<String> getNextCode() {
		ResultClass<String> result = new ResultClass<String>();
		result.setE(daoDegree.getNextCode());
		return result;

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public ResultClass<Degree> getDegreeAll(Long id) {
		ResultClass<Degree> result = new ResultClass<Degree>();
		
		Degree d = daoDegree.getDegree(id);
		d.setModules(serviceModule.getModulesForDegree(id).getE());
		d.setCompetences(serviceCompetence.getCompetencesForDegree(id));
		result.setE(d);
		return result;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@Transactional(readOnly = false)
	public ResultClass<Boolean> unDeleteDegree(Degree degree) {
		Degree d = daoDegree.existByCode(degree.getInfo().getCode());
		ResultClass<Boolean> result = new ResultClass<Boolean>();
		if(d == null){
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<String>();
			errors.add("Code doesn't exist");
			result.setErrorsList(errors);

		}
		else{
			if(!d.getIsDeleted()){
				Collection<String> errors = new ArrayList<String>();
				errors.add("Code is not deleted");
				result.setErrorsList(errors);
			}

			d.setDeleted(false);
			d.setInfo(degree.getInfo());
			boolean r = daoDegree.saveDegree(d);
			if (r)
				result.setE(true);	

		}
		return result;
	}

}