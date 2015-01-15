package com.example.tfg.domain;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name="subject")
@Where(clause = "isDeleted='false'")
public class Subject {
	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_subject")
	private Long id;
	
	@Basic(optional=false)
	@Column(name = "name", length=50,nullable=false)
	private String name;
	
	@Basic(optional=false)
	@Column(name = "description", length=250,nullable=false)
	private String description;
	
	@Column(name = "isDeleted", nullable=false, columnDefinition="boolean default false")
	private boolean isDeleted;
	
	@ManyToOne(optional = false, fetch=FetchType.LAZY)//cascade= CascadeType.ALL)
	@JoinColumn(name = "id_degree", insertable=false, updatable=false)
	private Degree degree;
 
	@ManyToMany(cascade = {CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinTable(name="subject_competence", 
	                joinColumns={@JoinColumn(name="id_subject")}, 
	                inverseJoinColumns={@JoinColumn(name="id_competence")})
	private Collection<Competence> competences;
	
	@Column(name = "code_subject", nullable=false)
	private String code;



	public Subject() {
		 super();
	}
	

	public Degree getDegree() {
		return degree;
	}

	public void setDegree(Degree degree) {
		this.degree = degree;
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<Competence> getCompetences() {
		return competences;
	}

	public void setCompetences(Collection<Competence> competences) {
		this.competences = competences;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}


}
