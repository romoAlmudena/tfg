package com.example.tfg.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.tfg.domain.Degree;
import com.example.tfg.domain.Subject;

@Repository
public interface DegreeDao {

	public boolean addDegree(Degree degree);

	public List<Degree> getAll();

	public boolean saveSubject(Degree degree);

	public Degree getDegree(Long id);

//	public List<Degree> getDegreeByName(Degree degree);

	public boolean deleteDegree(Degree degree);

	public Degree getDegreeSubject(Subject p);

	public String getNextCode();

	public Degree existByCode(String code);
//	 public Degree getDegreeAll(Long id);


}
