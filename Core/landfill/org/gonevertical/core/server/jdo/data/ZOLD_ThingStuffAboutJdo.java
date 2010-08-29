package org.gonevertical.core.server.jdo.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.gonevertical.core.client.ui.admin.thing.ThingData;
import org.gonevertical.core.client.ui.admin.thingstuff.ThingStuffData;
import org.gonevertical.core.client.ui.admin.thingstuff.ThingStuffDataFilter;
import org.gonevertical.core.client.ui.admin.thingstuff.ThingStuffsData;
import org.gonevertical.core.server.ServerPersistence;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Deprecated
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true") class ZOLD_ThingStuffAboutJdo {

	@NotPersistent
	private static final Logger log = Logger.getLogger(ZOLD_ThingStuffAboutJdo.class.getName());

	@NotPersistent
	private ServerPersistence sp = null;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key thingStuffAboutIdKey;

	// who is the main parent
	@Persistent
	private long parentThingId;

	// parent
	@Persistent
	private long parentStuffId;

	// why kind of stuff, defined as type, is this type of stuff
	@Persistent
	private long thingStuffTypeId;

	// values that can be stored
	@Persistent
	private String value;

	@Persistent
	private Boolean valueBol;

	@Persistent
	private Double valueDouble;

	@Persistent
	private Long valueLong;

	// TODO - could stick this in valueLong, but wondering how to deal with timezone
	@Persistent 
	private Date valueDate;

	// when did this start in time
	@Persistent
	private Date startOf;

	// when did this end in time
	@Persistent
	private Date endOf;

	// order the list by this
	@Persistent
	private Double rank;

	// when this object was created
	@Persistent
	private Date dateCreated;

	// when this object was updated
	@Persistent
	private Date dateUpdated;

	// who created this object
	@Persistent
	private long createdByThingId;

	// who updated this object
	@Persistent
	private long updatedByThingId;

	// assign ownership of this thing to this thing
	@Persistent
	private long[] ownerThingIds;

	/**
	 * constructor 
	 * 
	 * @throws Exception
	 */
	@Deprecated
	private ZOLD_ThingStuffAboutJdo() throws Exception {
		//System.err.println("Don't use this constructor - Exiting");
		//throw new Exception();
	}

	/**
	 * constructor
	 */
	@Deprecated
	private ZOLD_ThingStuffAboutJdo(ServerPersistence sp) {
		this.sp = sp;
	}
	
	@Deprecated
	private void set(ServerPersistence sp) {
		this.sp = sp;
	}

	@Deprecated
	private void setAboutStuffData(ThingStuffData thingStuffData) {
		if (thingStuffData == null) {
			return;
		}
		
		// parent id
		this.parentStuffId = thingStuffData.getStuffId();

		this.thingStuffTypeId = thingStuffData.getStuffTypeId();
		this.parentThingId = thingStuffData.getParentThingId();

		this.value = thingStuffData.getValue();
		this.valueBol = thingStuffData.getValueBol();
		this.valueDouble = thingStuffData.getValueDouble();
		this.valueLong = thingStuffData.getValueLong();
		this.valueDate = thingStuffData.getValueDate(); 

		this.startOf = thingStuffData.getStartOf();
		this.endOf = thingStuffData.getEndOf();

		this.rank = thingStuffData.getRank();
		this.ownerThingIds = thingStuffData.getOwners();

		if (thingStuffData != null && thingStuffData.getParentStuffId() > 0) {
			this.dateUpdated = new Date();
			this.updatedByThingId = sp.getUserThingId();
		} else {
			this.dateCreated = new Date();
			this.createdByThingId = sp.getUserThingId();
		}
	}

	@Deprecated
	private void setAboutStuffData(ZOLD_ThingStuffAboutJdo thingStuffJdo) {
		if (thingStuffJdo == null) {
			return;
		}
		setKey(thingStuffJdo.getParentStuffId());

		// parent id
		this.parentStuffId = thingStuffJdo.getParentStuffId();

		this.thingStuffTypeId = thingStuffJdo.getThingStuffTypeId();
		this.parentThingId = thingStuffJdo.getThingId();

		this.value = thingStuffJdo.getValue();
		this.valueBol = thingStuffJdo.getValueBol();
		this.valueDouble = thingStuffJdo.getValueDouble();
		this.valueLong = thingStuffJdo.getValueLong();
		this.valueDate = thingStuffJdo.getValueDate();

		this.startOf = thingStuffJdo.getStartOf();
		this.endOf = thingStuffJdo.getEndOf();

		this.rank = thingStuffJdo.getRank();
		this.ownerThingIds = thingStuffJdo.getOwners();

		if (thingStuffAboutIdKey != null && thingStuffAboutIdKey.getId() > 0) {
			this.dateUpdated = new Date();
			this.updatedByThingId = sp.getUserThingId();
		} else {
			this.dateCreated = new Date();
			this.createdByThingId = sp.getUserThingId();
		}
	}

	private void setKey(long id) {
		if (id > 0) {
			thingStuffAboutIdKey = getKey(id);
		}
	}

	private long save(ThingStuffData thingStuffData) {
		setAboutStuffData(thingStuffData);

		PersistenceManager pm = sp.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();

			if (thingStuffData != null && thingStuffData.getParentStuffId() > 0) { // update
				ZOLD_ThingStuffAboutJdo update = pm.getObjectById(ZOLD_ThingStuffAboutJdo.class, thingStuffData.getParentStuffId());
				update.set(sp);
				update.setAboutStuffData(thingStuffData);				
				this.thingStuffAboutIdKey = update.thingStuffAboutIdKey;

			} else { // insert
				this.thingStuffAboutIdKey = null;
				pm.makePersistent(this);
			}

			tx.commit();

		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}

		// debug
		//System.out.println("ThingStuffAboutJdo.save(): thingStuffAboutId: " + getStuffAboutId() + " thingStuffId(Parent): " + thingStuffId + " thingStuffTypeId: " + thingStuffTypeId + " " +
		//"value: " + getString(value) + " valueBol: " + getString(valueBol) + " valueLong: " + getString(valueLong) + " valueDate: " + getString(valueDate));

		return getStuffAboutId();
	}

	private long saveUnique(ThingStuffData thingStuffData) {
		setAboutStuffData(thingStuffData);

		// setup filter so that I only create unique by identities [thingId, thingStuffTypeId)
		ThingStuffDataFilter filter = new ThingStuffDataFilter();
		filter.setParentThingId(thingStuffData.getParentThingId());
		filter.setStuffTypeId(thingStuffData.getStuffTypeId());
		filter.setStuffId(thingStuffData.getStuffId());

		if (thingStuffData.getValue() != null) {
			filter.setValueString(thingStuffData.getValue());
		}

		if (thingStuffData.getValueBol() != null) {
			filter.setValueBoolean(thingStuffData.getValueBol());
		}

		if (thingStuffData.getValueDouble() != null) {
			filter.setValueDouble(thingStuffData.getValueDouble());
		}

		if (thingStuffData.getValueLong() != null) {
			filter.setValueLong(thingStuffData.getValueLong());
		}

		ThingStuffData[] tsds = query(filter);
		if (tsds != null && tsds.length > 0) {
			ThingStuffData tsd = tsds[0];
			tsds[0].setValue(thingStuffData.getValue());
			tsds[0].setValue(thingStuffData.getValueBol());
			tsds[0].setValue(thingStuffData.getValueDouble());
			tsds[0].setValue(thingStuffData.getValueLong());
			tsds[0].setValue(thingStuffData.getValueDate());

			save(tsd);
			return thingStuffData.getStuffId();
		}

		PersistenceManager pm = sp.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();

			thingStuffAboutIdKey = null;
			pm.makePersistent(this);

			tx.commit();

		} catch (Exception e) { 
			e.printStackTrace();
			log.log(Level.SEVERE, "saveUnique()", e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}

		// debug
		//System.out.println("ThingJdo: thingStuffId: " + getId() + " thingStuffTypeId: " + thingStuffTypeId + " " +
		//"value: " + getString(value) + " valueBol: " + getString(valueBol) + " valueLong: " + getString(valueLong) + " valueDate: " + getString(valueDate));

		return getStuffAboutId();
	}

	/**
	 * query a thing by its id
	 * 
	 * @param thingStuffId
	 * @return
	 */
	private ZOLD_ThingStuffAboutJdo query(long thingStuffId) {

		ZOLD_ThingStuffAboutJdo thingStuff = null;
		PersistenceManager pm = sp.getPersistenceManager();
		try {
			thingStuff = pm.getObjectById(ZOLD_ThingStuffAboutJdo.class, thingStuffId);
		} finally {
			pm.close();
		}

		return thingStuff;
	}

	/**
	 * get stuff about - by stuffId(parent)
	 * @param filter
	 * @return
	 */
	private ThingStuffData[] query(ThingStuffDataFilter filter) {

		ArrayList<ZOLD_ThingStuffAboutJdo> aT = new ArrayList<ZOLD_ThingStuffAboutJdo>();

		String qfilter = filter.getFilter_And();

		PersistenceManager pm = sp.getPersistenceManager();
		try {
			Extent<ZOLD_ThingStuffAboutJdo> e = pm.getExtent(ZOLD_ThingStuffAboutJdo.class, true);
			Query q = pm.newQuery(e, qfilter);
			q.execute();

			Collection<ZOLD_ThingStuffAboutJdo> c = (Collection<ZOLD_ThingStuffAboutJdo>) q.execute();
			Iterator<ZOLD_ThingStuffAboutJdo> iter = c.iterator();
			while (iter.hasNext()) {
				ZOLD_ThingStuffAboutJdo t = (ZOLD_ThingStuffAboutJdo) iter.next();
				aT.add(t);
			}

			q.closeAll();
		} finally {
			pm.close();
		}

		ZOLD_ThingStuffAboutJdo[] tj = new ZOLD_ThingStuffAboutJdo[aT.size()];
		if (aT.size() > 0) {
			tj = new ZOLD_ThingStuffAboutJdo[aT.size()];
			aT.toArray(tj);
		}

		// TODO overkill here - can get the list up above
		List<ZOLD_ThingStuffAboutJdo> tjsa_list = Arrays.asList(tj);

		ThingStuffData[] td = convert(tjsa_list);

		return td;
	}

	/**
	 * query total
	 * 
	 *  TODO API not ready to do this effectively in hosted mode yet
	 * 
	 * @return
	 */
	private long queryTotal() {

		/* future spec I think
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query("__Stat_Kind__");
		query.addFilter("kind_name", FilterOperator.EQUAL, TThingStuffAboutJdo.class);

    Entity globalStat = datastore.prepare(query).asSingleEntity();
    Long totalBytes = (Long) globalStat.getProperty("bytes");
    Long totalEntities = (Long) globalStat.getProperty("count");
		 */

		// TODO - work around, have to wait for the api/gae to make it to hosted mode
		long total = 0;

		PersistenceManager pm = sp.getPersistenceManager();
		try {
			Extent<ZOLD_ThingStuffAboutJdo> e = pm.getExtent(ZOLD_ThingStuffAboutJdo.class, true);
			Query q = pm.newQuery(e);
			q.execute();

			Collection<ZOLD_ThingStuffAboutJdo> c = (Collection<ZOLD_ThingStuffAboutJdo>) q.execute();
			total = c.size();

			q.closeAll();
		} finally {
			pm.close();
		}

		return total;
	}

	private static ThingStuffData[] convert(List<ZOLD_ThingStuffAboutJdo> thingStuffJdoAbout) {

		Iterator<ZOLD_ThingStuffAboutJdo> itr = thingStuffJdoAbout.iterator();

		ThingStuffData[] r = new ThingStuffData[thingStuffJdoAbout.size()];

		int i = 0;
		while(itr.hasNext()) {

			//ThingStuffAboutJdo tsja = itr.next();

			//r[i] = new ThingStuffData();
			//r[i].setData(
					//tsja.getThingId(),
					//tsja.getParentStuffId(),
					
					//tsja.getStuffAboutId(),
					//tsja.getStuffTypeId(), 

					//tsja.getValue(), 
					//tsja.getValueBol(), 
					//tsja.getValueDouble(),
					//tsja.getValueLong(), 
					//tsja.getValueDate(),

					//tsja.getStartOf(),
					//tsja.getEndOf(), 
					//tsja.getRank(),
					//tsja.getCreatedBy(),
					//tsja.getDateCreated(),
					//tsja.getUpdatedBy(),
					//tsja.getDateUpdated(),
					//tsja.getOwners());

			i++;
		}

		return r;
	}

	private List<ZOLD_ThingStuffAboutJdo> convertStuffsAboutToJdo(ThingStuffsData thingStuffsData) {

		if (thingStuffsData == null) {
			return null;
		}

		ThingStuffData[] tsd = thingStuffsData.getThingStuffData();

		ZOLD_ThingStuffAboutJdo[] r = new ZOLD_ThingStuffAboutJdo[tsd.length];

		for (int i=0; i < tsd.length; i++) {
			r[i] = new ZOLD_ThingStuffAboutJdo(sp);

			r[i].parentThingId = tsd[i].getParentThingId();
			r[i].parentStuffId = tsd[i].getStuffId();
			r[i].thingStuffAboutIdKey = getKey(tsd[i].getStuffId());
			r[i].thingStuffTypeId = tsd[i].getStuffTypeId();

			r[i].value = tsd[i].getValue();
			r[i].valueBol = tsd[i].getValueBol();
			r[i].valueDouble = tsd[i].getValueDouble();
			r[i].valueLong = tsd[i].getValueLong();

			r[i].startOf = tsd[i].getStartOf();
			r[i].endOf = tsd[i].getEndOf();
			r[i].rank = tsd[i].getRank();
			
			r[i].createdByThingId = tsd[i].getCreatedBy();
			r[i].dateCreated = tsd[i].getDateCreated();
			r[i].updatedByThingId = tsd[i].getUpdatedBy();
			r[i].dateUpdated = tsd[i].getDateUpdated();
			r[i].ownerThingIds = tsd[i].getOwners();

		}

		List<ZOLD_ThingStuffAboutJdo> l = Arrays.asList(r);

		return l;
	}

	/**
	 * delete thing about the about id
	 * 
	 * @param stuffId
	 * @return
	 */
	private boolean delete(long stuffId) {

		System.out.println("ThingStuffAboutJdo.delete(): deleting: " + stuffId);

		PersistenceManager pm = sp.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		boolean b = false;
		try {
			tx.begin();

			ZOLD_ThingStuffAboutJdo ttj2 = (ZOLD_ThingStuffAboutJdo) pm.getObjectById(ZOLD_ThingStuffAboutJdo.class, stuffId);
			pm.deletePersistent(ttj2);

			tx.commit();
			b = true;
		} catch (Exception e) {
			e.printStackTrace();
			b = false;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
				b = false;
			}
			pm.close();
		}

		return b;
	}

	private boolean delete(ThingData thingData) {

		if (thingData == null) {
			return false;
		}

		long thingId = thingData.getThingId();

		if (thingId == 0) {
			return false;
		}

		String qfilter = "thingId==" + thingId + "";

		PersistenceManager pm = sp.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();

			Extent<ZOLD_ThingStuffAboutJdo> e = pm.getExtent(ZOLD_ThingStuffAboutJdo.class, true);
			Query q = pm.newQuery(e, qfilter);
			q.execute();

			Collection<ZOLD_ThingStuffAboutJdo> c = (Collection<ZOLD_ThingStuffAboutJdo>) q.execute();

			// delete all
			pm.deletePersistentAll(c);

			tx.commit();
			q.closeAll();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}

		return true;
	}

	/**
	 * delete by parent id, thingStuffId
	 * 
	 * @param thingStuffId
	 * @return
	 */
	private boolean deleteByParent(long thingStuffId) {

		String qfilter = "thingStuffId==" + thingStuffId + "";

		PersistenceManager pm = sp.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();

			Extent<ZOLD_ThingStuffAboutJdo> e = pm.getExtent(ZOLD_ThingStuffAboutJdo.class, true);
			Query q = pm.newQuery(e, qfilter);
			q.execute();

			Collection<ZOLD_ThingStuffAboutJdo> c = (Collection<ZOLD_ThingStuffAboutJdo>) q.execute();

			// delete all
			pm.deletePersistentAll(c);

			tx.commit();
			q.closeAll();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}

		return true;
	}

	private long getStuffAboutId() {
		if (thingStuffAboutIdKey == null) {
			return -1;
		}
		return thingStuffAboutIdKey.getId();
	}

	/**
	 * parent of this object (owner of this object)
	 * 
	 * @return
	 */
	private long getParentStuffId() {
		return parentStuffId;
	}

	private long getStuffTypeId() {
		return thingStuffTypeId;
	}

	private long getThingId() {
		return parentThingId;
	}

	private long getThingStuffTypeId() {
		return thingStuffTypeId;
	}

	private void setValue(String value) {
		this.value = value;
	}

	private void setValue(Boolean value) {
		this.valueBol = value;
	}

	private void setValue(Double value) {
		this.valueDouble = value;
	}

	private String getValue() {
		return value;
	}

	private Boolean getValueBol() {
		return valueBol;
	}

	private Double getValueDouble() {
		return valueDouble;
	}

	private Long getValueLong() {
		return valueLong;
	}

	private Date getValueDate() {
		return valueDate;
	}

	private Date getStartOf() {
		return startOf;
	}

	private Date getEndOf() {
		return endOf;
	}

	private void setRank(Double rank) {
		this.rank = rank;
	}

	private Double getRank() {
		return rank;
	}

	private Date getDateCreated() {
		return dateCreated;
	}

	private Date getDateUpdated() {
		return dateUpdated;
	}

	private long getCreatedBy() {
		return createdByThingId;
	}

	private long getUpdatedBy() {
		return updatedByThingId;
	}

	private void setOwners(long[] ownerThingIds) {
		this.ownerThingIds = ownerThingIds;
	}

	private long[] getOwners() {
		return ownerThingIds;
	}

	private Key getKey(long id) {
		Key key = null;
		if (id == 0) {
			key = KeyFactory.createKey(ZOLD_ThingStuffAboutJdo.class.getSimpleName(), id);
		}
		return key;
	}



	private String getString(Boolean value) {
		String s = "";
		if (value != null) {
			s = value.toString();
		}
		return s;
	}

	private String getString(Date value) {
		String s = "";
		if (value != null) {
			s = value.toString();
		}
		return s;
	}

	private String getString(Long value) {
		String s = "";
		if (value != null) {
			s = Long.toString(value);
		}
		return s;
	}

	private String getString(String value) {
		String s = "";
		if (value != null) {
			s = value;
		}
		return s;
	}
	
	
}