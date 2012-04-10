package gov.usgs.ngwmn.dm.spec;

import gov.usgs.ngwmn.dm.dao.WellRegistry;
import gov.usgs.ngwmn.dm.dao.WellRegistryDAO;
import gov.usgs.ngwmn.dm.dao.WellRegistryExample;
import gov.usgs.ngwmn.dm.dao.WellRegistryExample.Criteria;

import java.util.Iterator;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class LatLongResolver implements SpecResolver {

	WellRegistryDAO dao;
	
	private Specification specification;

	
	public LatLongResolver(Specification spec) {
		specification = spec;
	}
	
	public Iterator<Specifier> iterator() {
		Specification spec = specification;
		
		WellRegistryExample example = new WellRegistryExample();
		Criteria criteria = example.createCriteria();
		criteria.andDecLatVaBetween(spec.getLatitudeSouth(), spec.getLatitudeNorth());
		criteria.andDecLongVaBetween(spec.getLongitudeEast(), spec.getLongitudeWest());
	
		final List<WellRegistry> wellList = dao.selectByExample(example);
		
		// TODO what to do on exception?
		// TODO return null or empty iterator on no data or exception?
		// TODO this does not address agency yet
		
		return new Iterator<Specifier>() {
			private Iterator<WellRegistry> wells = wellList.iterator();

			@Override
			public boolean hasNext() {
				return wells.hasNext();
			}

			@Override
			public Specifier next() {
				// if we do not check the hasNext here then the error on next call
				// will be transmitted to the caller. Note that if a null is returned
				// then we will throw an NPE because of the specifier constructor.
				Specifier spec = new Specifier( wells.next() );
				return spec;
			}

			@Override
			/** Cannot remove an entry from this collection. **/
			public void remove() {
				throw new NotImplementedException();
			}
		};
	}

}
