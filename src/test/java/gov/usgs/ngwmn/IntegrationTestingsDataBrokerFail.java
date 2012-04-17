
/*
		if (isTestSpecifier(spec)) {
			success = configureTestInput(spec, pipe);
		}
		private boolean isTestSpecifier(Specifier spec) {
			if ("TEST_INPUT_ERROR".equals(spec.getAgencyID())) {
				return true;
			}
			return false;
		}
		private boolean configureTestInput(Specifier spec, Pipeline pipe) 
				throws Exception 
		{
			if ("TEST_INPUT_ERROR".equals(spec.getAgencyID())) {
				DataFetcher unFetcher = new ErrorFetcher();
				configureInput(unFetcher, spec, pipe);
				return true;
			}
			return false;
		}

 */

