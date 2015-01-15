package com.example.tfg.domain;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "academicterm")
@Where(clause = "isDeleted='false'")
public class AcademicTerm {
	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_academicterm")
	private Long id;
	
	@Basic(optional=false)
	@Column(name = "term", nullable=false, columnDefinition="varchar(20) default '2014/2015'")
	private String term;
	
	@Column(name = "isDeleted", nullable=false, columnDefinition="boolean default false")
	private boolean isDeleted;
	
	@Column(name="idDegree")
	private Degree degree;
	

	@OneToMany(mappedBy="academicTerm", cascade= CascadeType.ALL)//, orphanRemoval=true)
	private Collection<Course> courses;
	
	public AcademicTerm() {
		super();
	}

	public Collection<Course> getCourses() {
		return courses;
	}

	public void setCourses(Collection<Course> courses) {
		this.courses = courses;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Degree getDegree() {
		return degree;
	}

	public void setDegree(Degree degree) {
		this.degree = degree;
	}
	
	@PostLoad
	public void postLoad(){
	    try {
	        if(getDegree() != null && getDegree().getId() == 0){
	            setDegree(null);
	        }
	    }
	    catch (EntityNotFoundException e){
	        setDegree(null);
	    }
	} 
}
