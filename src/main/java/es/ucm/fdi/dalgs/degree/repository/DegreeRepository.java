package es.ucm.fdi.dalgs.degree.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import es.ucm.fdi.dalgs.domain.Degree;
import es.ucm.fdi.dalgs.domain.Subject;

@Repository
public class DegreeRepository {
	protected EntityManager em;

	public EntityManager getEntityManager() {
		return em;
	}

	protected static final Logger logger = LoggerFactory
			.getLogger(DegreeRepository.class);

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		try {
			this.em = entityManager;
		} catch (Exception e) {
			logger.error(e.getMessage());

		}
	}
	
	public boolean addDegree(Degree degree) {
		try {
			em.persist(degree);
		} catch (PersistenceException e) {
			logger.error(e.getMessage());
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public List<Degree> getAll() {

		return em.createQuery("select d from Degree d where d.isDeleted = false order by d.id ")
				.getResultList();

	}

	
	public boolean saveDegree(Degree degree) {
		try {
			em.merge(degree);
		} catch (ConstraintViolationException e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	
	public Degree getDegree(Long id) {

		return em.find(Degree.class, id);
	}

	
	public boolean deleteDegree(Degree degree) {
		// Degree degree = em.getReference(Degree.class, id);
		try {
			degree.setDeleted(true);
			em.merge(degree);

			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	
	public Degree getDegreeSubject(Subject p) {
		Query query = em
				.createQuery("select d from Degree d join d.subjects s where s=?1");
		query.setParameter(1, p);
		if (query.getResultList().isEmpty())
			return null;
		return (Degree) query.getResultList().get(0);
	}

	public String getNextCode() {
		Query query = em.createQuery("Select MAX(e.id ) from Degree e");
		try {
			Long aux = (Long) query.getSingleResult() + 1;
			return "DEG" + aux;
		} catch (Exception e) {
			logger.error(e.getMessage());

			return null;
		}

	}

	public Degree existByCode(String code) {
		Query query = em.createQuery("select d from Degree d where d.info.code=?1");
		query.setParameter(1, code);
		 if (query.getResultList().isEmpty())
		 	return null;
		 else return (Degree) query.getSingleResult();
	}
}