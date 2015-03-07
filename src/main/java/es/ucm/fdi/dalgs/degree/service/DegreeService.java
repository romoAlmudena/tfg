package es.ucm.fdi.dalgs.degree.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ucm.fdi.dalgs.academicTerm.service.AcademicTermService;
import es.ucm.fdi.dalgs.acl.service.AclObjectService;
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
	private AclObjectService manageAclService;
	
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
		Collection<String> errors =new ArrayList<String>();
		if( degreeExists != null){
			result.setHasErrors(true);
			errors.add("Code already exists");

			if (degreeExists.getIsDeleted()){
				result.setElementDeleted(true);
				errors.add("Element is deleted");

			}
			result.setErrorsList(errors);
		}
		else{
			boolean r = daoDegree.addDegree(degree);
			
			degreeExists = daoDegree.existByCode(degree.getInfo().getCode());
			if( degreeExists != null){
				manageAclService.addAclToObject(degreeExists.getId(), degreeExists.getClass().getName());
			} else {
				errors.add("Cannot create ACL. Object not set");
			}
			
			if (r) result.setE(true);
		}
		

		
		
		
		return result;

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostFilter("hasPermission(filterObject, 'READ')")
	@Transactional(readOnly = true)
	public List<Degree> getDegrees(Integer pageIndex, Boolean showAll) {
		return daoDegree.getDegrees(pageIndex, showAll);
	}
	

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostFilter("hasPermission(filterObject, 'READ')")
	@Transactional(readOnly = true)
	public List<Degree> getAll() {
		return daoDegree.getAll();
	}

	@PreAuthorize("hasPermission(#degree, 'WRITE') or hasPermission(#degree, 'ADMINISTRATION')")
	@Transactional(readOnly = false)
	public ResultClass<Boolean> modifyDegree(Degree degree) {
		ResultClass<Boolean> result = new ResultClass<Boolean>();

		
		Degree degreeExists = daoDegree.existByCode(degree.getInfo().getCode());
		
		if(!degree.getInfo().getCode().equalsIgnoreCase(degree.getInfo().getCode()) && 
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
			boolean r = daoDegree.saveDegree(degree);
			if (r) 
				result.setE(true);
		}
		return result;

		
	}


	@PreAuthorize("hasPermission(#degree, 'DELETE') or hasPermission(#degree, 'ADMINISTRATION')" )
	@Transactional(readOnly = false)
	public boolean deleteDegree(Degree degree) {
		boolean deleteModules = false;
		boolean deleteCompetences = false;
		boolean deleteAcademic = false;

		if (!degree.getModules().isEmpty())
			deleteModules = serviceModule.deleteModulesForDegree(degree);
		if (!degree.getCompetences().isEmpty())
			deleteCompetences = serviceCompetence.deleteCompetencesForDegree(degree);
		Collection<AcademicTerm> academicList = serviceAcademicTerm.getAcademicTermsByDegree(degree.getId());


		if(!academicList.isEmpty()) deleteAcademic = serviceAcademicTerm.deleteAcademicTermCollection(academicList);
		if ((deleteModules || degree.getModules().isEmpty()) && (deleteCompetences || degree.getCompetences().isEmpty())
				&& (deleteAcademic || academicList.isEmpty())){
				
			return daoDegree.deleteDegree(degree);
		} else
			return false;
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public Degree getDegreeSubject(Subject p) {

		return daoDegree.getDegreeSubject(p);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public String getNextCode() {
		return daoDegree.getNextCode();

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostAuthorize("hasPermission(returnObject, 'READ')")
	@Transactional(readOnly = true)
	public Degree getDegree(Long id) {
		Degree d = daoDegree.getDegree(id);
		d.setModules(serviceModule.getModulesForDegree(id));
		d.setCompetences(serviceCompetence.getCompetencesForDegree(id));
		return d;
	}

	@PreAuthorize("hasPermission(#degree, 'DELETE') or hasPermission(#degree, 'ADMINISTRATION')" )
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
	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public Integer numberOfPages(Boolean showAll) {
		return daoDegree.numberOfPages(showAll);
	}

}
