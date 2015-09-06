package hu.akoel.restful;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="users")
@XmlRootElement
public class UserEntity{
	private Integer id;
	private String name;
	private String password;

	public UserEntity() {
	}

	public UserEntity(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "user_id_seq_gen")
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "user_id_seq_gen", sequenceName = "user_id_seq")
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false, unique=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String toString(){
		StringBuilder result = new StringBuilder();
		
		result.append(  "Id: " + this.id );
		result.append( ", Name: " + this.name );
		result.append(  ", Password: " + this.password );
		return result.toString();
	}
}