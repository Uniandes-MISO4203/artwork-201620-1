/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.artwork.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import co.edu.uniandes.csw.crud.spi.entity.BaseEntity;
import uk.co.jemos.podam.common.PodamExclude;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ArtworkEntity extends BaseEntity implements Serializable {

    private String image;
    private Long price;
    private Integer width;
    private Integer height;
    
    @Temporal(TemporalType.DATE)
    private Date dateAdded;

    @PodamExclude
    @OneToMany
    private List<CategoryEntity> category = new ArrayList<>();

    @PodamExclude
    @ManyToOne
    private ArtistEntity artist;

    @PodamExclude
    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QualificationEntity> qualifications = new ArrayList<>();
     
    @PodamExclude
    @OneToMany(mappedBy = "artwork")
    private List<CommentEntity> comments = new ArrayList<>();
    
    @PodamExclude
    @OneToMany(mappedBy = "artwork")
    private List<PrizeEntity> prizes;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public ArtistEntity getArtist() {
        return artist;
    }

    public void setArtist(ArtistEntity artist) {
        this.artist = artist;
    }

    public List<CategoryEntity> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryEntity> category) {
        this.category = category;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public List<QualificationEntity> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<QualificationEntity> qualifications) {
        this.qualifications = qualifications;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }
    
    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

 /**
  * @return the prizes
  */
 public List<PrizeEntity> getPrizes() {
  return prizes;
 }

 /**
  * @param prizes the prizes to set
  */
 public void setPrizes(List<PrizeEntity> prizes) {
  this.prizes = prizes;
 }

 /**
  * @return the prizes
  */
 

 /**
  * @return the artworks
  */
 

 

}
