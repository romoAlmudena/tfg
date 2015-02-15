package com.example.tfg.repository.implementation;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.example.tfg.domain.Degree;
import com.example.tfg.domain.Module;
import com.example.tfg.repository.ModuleDao;

@Repository
public class ModuleDaoImp implements ModuleDao {


	protected EntityManager em;

	public EntityManager getEntityManager() {
		return em;
	}

	protected static final Logger logger = LoggerFactory
			.getLogger(ModuleDaoImp.class);

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		try {
			this.em = entityManager;
		} catch (Exception e) {
			logger.error(e.getMessage());

		}
	}
	
	public boolean addModule(Module module) {
		try {
			em.persist(module);
		} catch (ConstraintViolationException e) {
			logger.error(e.getMessage());
			return false;
		}

		return true;
	}


	@SuppressWarnings("unchecked")
	public List<Module> getAll() {
		return em.createQuery("select m from Module m where m.isDeleted = false order by m.id ")
				.getResultList();
	}


	public boolean saveModule(Module module) {
		try {
			em.merge(module);
		} catch (ConstraintViolationException e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	
	public Module getModule(Long id) {
		return em.find(Module.class, id);
	}

	
	public boolean deleteModule(Long id_module) {
		try {
			Module module = em.getReference(Module.class, id_module);
			module.setDeleted(true);
			em.merge(module);

			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	@Override
	public String getNextCode() {
		// TODO Auto-generated method stub
		return null;
	}


	public Module existByCode(String code) {
		Query query = em.createQuery("select m from Module m where m.info.code=?1");
		query.setParameter(1, code);
		 if (query.getResultList().isEmpty())
		 	return null;
		 else return (Module) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")

	public Collection<Module> getModulesForDegree(Long id) {
		Degree degree = em.getReference(Degree.class, id);

		Query query = em
				.createQuery("select m from Module m where m.degree=?1 and m.isDeleted='false'");
		query.setParameter(1, degree);

		if (query.getResultList().isEmpty())
			return null;
		return (List<Module>) query.getResultList();
		
	}

	

}