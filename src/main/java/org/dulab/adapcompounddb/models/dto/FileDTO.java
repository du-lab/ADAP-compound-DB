package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Entity;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.FileType;
import org.dulab.adapcompounddb.site.services.SpectrumSearchService;

@Entity
public class FileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private String name;

    private FileType fileType;

    private byte[] content;
    
    SubmissionDTO submission;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public Map<ChromatographyType, SpectrumSearchService> getSpectra() {
		// TODO Auto-generated method stub
		return null;
	}

	public SubmissionDTO getSubmission() {
		return submission;
	}

	public void setSubmission(SubmissionDTO submission) {
		this.submission = submission;
	}

}
