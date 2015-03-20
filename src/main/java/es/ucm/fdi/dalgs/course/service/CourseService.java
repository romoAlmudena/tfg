package es.ucm.fdi.dalgs.course.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import es.ucm.fdi.dalgs.academicTerm.service.AcademicTermService;
import es.ucm.fdi.dalgs.acl.service.AclObjectService;
import es.ucm.fdi.dalgs.activity.service.ActivityService;
import es.ucm.fdi.dalgs.classes.ResultClass;
import es.ucm.fdi.dalgs.course.repository.CourseRepository;
import es.ucm.fdi.dalgs.domain.AcademicTerm;
import es.ucm.fdi.dalgs.domain.Course;
import es.ucm.fdi.dalgs.group.service.GroupService;

@Service
public class CourseService {
	@Autowired
	private CourseRepository daoCourse;

	@Autowired
	private AclObjectService manageAclService;
	
	@Autowired
	ActivityService serviceActivity;
	
	@Autowired
	GroupService serviceGroup;

	// @Autowired
	// private DegreeService serviceDegree;

	@Autowired
	private AcademicTermService serviceAcademicTerm;

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@Transactional(readOnly = false)
	public ResultClass<Course> addCourse(Course course, Long id_academic) {
		
		boolean success = false;
		
		course.setAcademicTerm(serviceAcademicTerm.getAcademicTerm(id_academic,false).getSingleElement());
		Course courseExists = daoCourse.exist(course);
		ResultClass<Course> result = new ResultClass<Course>();

		if( courseExists != null){
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<String>();
			errors.add("Code already exists");

			if (courseExists.getIsDeleted()){
				result.setElementDeleted(true);
				errors.add("Element is deleted");
				result.setSingleElement(courseExists);

			}
			result.setSingleElement(course);
			result.setErrorsList(errors);
		}
		else{
		
			success = daoCourse.addCourse(course);
				

				if(success){
					courseExists = daoCourse.exist(course);
					success = manageAclService.addAclToObject(courseExists.getId(), courseExists.getClass().getName());
					if (success)result.setSingleElement(course);

				} else {
					throw new IllegalArgumentException(	"Cannot create ACL. Object not set.");
				}
			}
					
		return result;		
	
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public List<Course> getAll() {
		return daoCourse.getAll();
	}



	@PreAuthorize("hasPermission(#course, 'WRITE') or hasPermission(#course, 'ADMINISTRATION')")
	@Transactional(readOnly = false)
	public ResultClass<Boolean> modifyCourse(Course course, Long id_academic, Long id_course) {
		ResultClass<Boolean> result = new ResultClass<Boolean>();
		
		course.setAcademicTerm(serviceAcademicTerm.getAcademicTerm(id_academic, false).getSingleElement());
		
		Course modifyCourse = daoCourse.getCourse(id_course);
		
		Course courseExists = daoCourse.exist(course);
		
		if(!course.getSubject().equals(modifyCourse.getSubject()) && 
				courseExists != null){
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<String>();
			errors.add("New code already exists");

			if (courseExists.getIsDeleted()){
				result.setElementDeleted(true);
				errors.add("Element is deleted");

			}
			result.setErrorsList(errors);
			result.setSingleElement(false);
		}
		else{
			modifyCourse.setSubject(course.getSubject());
			modifyCourse.setCoordinator(course.getCoordinator());
			boolean r = daoCourse.saveCourse(modifyCourse);
			if (r) {
				result.setSingleElement(true);
				// Adding the authorities to the professor list
				manageAclService.addPermissionToAnObjectCoordinator(course.getCoordinator(),course.getId(), course.getClass().getName());
			}
		}
		return result;

			

	}

	@PreAuthorize("hasRole('ROLE_USER')")
//	@PostFilter("hasPermission(filterObject, 'READ')")
	@Transactional(readOnly = true)
	public ResultClass<Course> getCourse(Long id) {
		ResultClass<Course> result = new ResultClass<Course>();
		result.setSingleElement(daoCourse.getCourse(id));
		return result;
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
//	@PreAuthorize("hasPermission(#academicTerm, 'DELETE') or hasPermission(#academicTerm, 'ADMINISTRATION')")
	@Transactional(propagation = Propagation.REQUIRED)
	public ResultClass<Boolean> deleteCourse(Long id) {
		ResultClass<Boolean> result = new ResultClass<Boolean>();
		Course course = daoCourse.getCourse(id);
		if (serviceActivity.deleteActivitiesFromCourse(course).getSingleElement()){
			result.setSingleElement(daoCourse.deleteCourse(course));
			return result;
		}
		result.setSingleElement(false);
		return result;
	}

	
	@PreAuthorize("hasRole('ROLE_USER')")
//	@PostFilter("hasPermission(filterObject, 'READ')")
	public ResultClass<Course> getCoursesByAcademicTerm(Long id_academic, Boolean showAll) {
		ResultClass<Course> result = new ResultClass<>();

		result.addAll(daoCourse.getCoursesByAcademicTerm(id_academic, showAll));
		return result;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")		
	public ResultClass<Boolean> deleteCoursesFromAcademic(AcademicTerm academic) {
		ResultClass<Boolean> result = new ResultClass<>();
		boolean deleteActivities = serviceActivity.deleteActivitiesFromCourses(academic.getCourses()).getSingleElement();
		boolean deleteGroups = serviceGroup.deleteGroupsFromCourses(academic.getCourses()).getSingleElement();
		if (deleteActivities && deleteGroups){
				result.setSingleElement(daoCourse.deleteCoursesFromAcademic(academic));
		}
		else result.setSingleElement(false);
			
		return result;

	}
	


	
	@PreAuthorize("hasRole('ROLE_USER')")
	@PostFilter("hasPermission(filterObject, 'READ')")
	@Transactional(readOnly = true)
	public ResultClass<Course> getCourseAll(Long id, Boolean showAll) {
		ResultClass<Course> result = new ResultClass<>();
		Course c = daoCourse.getCourse(id);
		c.setActivities(serviceActivity.getActivitiesForCourse(id, showAll));
		c.setGroups(serviceGroup.getGroupsForCourse(id, showAll));
		
		result.setSingleElement(c);
	
		return result;
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostFilter("hasPermission(filterObject, 'READ')")
	public ResultClass<Course> getCoursesfromListAcademic(
			Collection<AcademicTerm> academicList) {
		
		ResultClass<Course> result = new ResultClass<>();
		result.addAll(daoCourse.getCoursesFromListAcademic(academicList));
		
		return result;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	public ResultClass<Boolean> deleteCourses(Collection<AcademicTerm> academicList) {
		
		ResultClass<Boolean> result = new ResultClass<>();
		
		Collection<Course> coursesList = daoCourse.getCoursesFromListAcademic(academicList);
		boolean deleteActivities = serviceActivity.deleteActivitiesFromCourses(coursesList).getSingleElement();
		boolean deleteGroups = serviceGroup.deleteGroupsFromCourses(coursesList).getSingleElement();
		if (deleteActivities && deleteGroups){
			result.setSingleElement(daoCourse.deleteCourses(academicList));
		}
		else result.setSingleElement(false);
		
		return result;
	}


//	public ResultClass<Boolean> modifyCourse(Course course) {
//		ResultClass<Boolean> result = new ResultClass<Boolean>();
//		result.setSingleElement(daoCourse.saveCourse(course));
//		return result;
//	}

//	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@PreAuthorize("hasPermission(#course, 'WRITE') or hasPermission(#course, 'ADMINISTRATION')")
	@Transactional(readOnly = false)
	public ResultClass<Course> unDeleteCourse(Course course, Long id_academic) {
		course.setAcademicTerm(serviceAcademicTerm.getAcademicTerm(id_academic, false).getSingleElement());
		Course c = daoCourse.exist(course);
		ResultClass<Course> result = new ResultClass<>();
		if(c == null){
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<String>();
			errors.add("Element doesn't exist");
			result.setErrorsList(errors);

		}
		else{
			if(!c.getIsDeleted()){
				Collection<String> errors = new ArrayList<String>();
				errors.add("Code is not deleted");
				result.setErrorsList(errors);
			}

			c.setDeleted(false);
			c.setSubject(course.getSubject());
			boolean r = daoCourse.saveCourse(c);
			if(r) 
				result.setSingleElement(c);	

		}
		return result;
	}
	
	
}
