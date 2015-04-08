#Zeratul

a wrapper for JPA

##Usage

#### use BaseModel and BaseDAO into your project
* extends BaseModel when you create your models like:

```
@Entity
@Table(name = "my_model")
public class Classmate extends BaseModel {
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 12, nullable = false)
    private gender;
    
    @Column(name = "mobile", length = 16)
    private String mobile;
    
    @Column(name = "birthday")
    private Date birthday;
    
    @Column(name = "gpa")
    private Double gpa;
    
    @Column(name = "grade")
    private Integer grade;
    ...
}
```
BaseModel declaration `id` and `time_created`:

```
@MappedSuperclass
public abstract class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time_created", nullable = false, updatable = false)
    private Date timeCreated;

    protected BaseModel() {
        timeCreated = DateTime.now().toDate();
    }
```
* extends BaseDAO when you create your DAO like:

```
public class ClassmateDAO extends BaseDAO<Classmate> {
    public ClassmateDAO() {
        super(Classmate);
    }
    ...
}
``` 

#### simpler query
* query single entity and filter by name and birthday between startTime and endTime:

```
public Classmate getClassmateByNameAndBirthday(String name, Date startTime, Date endTime)
    return querySingleResult(field("name").eq(name), field("birthday").bewteen(startTime, endTime));
}
```
* query list entity

```
public List<Classmate> listClassmateByName(String name) {
    return queryListResult(field("name").eq(name));
}
```
* query page entity

```
public List<Classmate> listClassmateByName(String name, int pageSize, int pageIndex) {
    return queryPageResult(pageSize, pageIndex, field("name").eq(name));
}
```
* query fields

```
public String getClassmateNameByBirthdayAboveTime(Date startTime) {
    return (String)querySingleResult(filter(value("name")), field("birthday").ge(startTime));
}
```
* advance query fields

```
public String getMaxGPA(Date startTime) {
    return (String)querySingleResult(filter(max("gpa")), field("birthday").ge(startTime));
}
```
* subquery 

```
queryListResult(field("mobile").in(subquery(People.class, "telephone", field("address").eq("china"))));
```

* distinct fields

```
queryListResult(distinct(value("gpa"), value("grade")), field("address").eq("china"))
```
...