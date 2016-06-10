/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.edtriageapp.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.module.edtriageapp.api.db.EdTriageAppDAO;

import java.util.Calendar;
import java.util.List;

/**
 * It is a default implementation of  {@link EdTriageAppDAO}.
 */
public class HibernateEdTriageAppDAO implements EdTriageAppDAO {

    private SessionFactory sessionFactory;

    /**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /*
    * gets all active encounters at a current location for a patient
    * */
    public List<Encounter> getActiveEncountersForPatientAtLocation(int hoursBack, String locationUuid, String patientId) {

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Encounter.class, "enc");
        //criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR, hoursBack * -1);
        criteria.add(Restrictions.ge("enc.encounterDatetime", now.getTime()));

        criteria.createAlias("enc.location", "loc");
        criteria.createAlias("enc.encounterType", "encType");

        criteria.add(Restrictions.eq("encType.uuid", EdTriageAppDAO.ENCOUNTER_TYPE_UUID));

        if (locationUuid != null && locationUuid.length() > 0) {
            criteria.add(Restrictions.eq("loc.uuid", locationUuid));
        }

        if (patientId != null && patientId.length() > 0) {
            criteria = criteria.createCriteria("patient", "pat");
            criteria.add(Restrictions.eq("pat.uuid", patientId));
        }

        criteria.addOrder(Order.desc("enc.encounterDatetime"));

        //noinspection unchecked
        return criteria.list();
    }

}