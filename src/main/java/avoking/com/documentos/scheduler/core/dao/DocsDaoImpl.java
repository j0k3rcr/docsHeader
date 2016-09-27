/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoking.com.documentos.scheduler.core.dao;

import avoking.com.conexiondao.BaseDaoHibernate;
import java.util.List;

public class DocsDaoImpl extends BaseDaoHibernate<Docs , String> implements DocsDao {

    @Override
    public List findAll() throws Exception {
        return getHibernateTemplate().loadAll(Docs.class);
    }

    @Override
    public Docs findById(String id) throws Exception {
        return getHibernateTemplate().get(Docs.class, id);
    }

}
