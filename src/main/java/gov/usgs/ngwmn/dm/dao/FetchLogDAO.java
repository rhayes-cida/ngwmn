package gov.usgs.ngwmn.dm.dao;

public class FetchLogDAO {
	private FetchLogMapper mapper;
	
	public FetchLogDAO(FetchLogMapper mapper) {
		super();
		this.mapper = mapper;
	}

	public void insert(FetchLog item) {
		mapper.insert(item);
	}
}
