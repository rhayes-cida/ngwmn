package gov.usgs.ngwmn.dm.harvest;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.util.Properties;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpringUrlFactory {

	private Properties myProps;
	
	public void setMapping(Properties pp) {
		myProps = pp;
		for (WellDataType type : WellDataType.values()) {
			String url = (String) pp.get( type.name() );
			if (url == null) {
				throw new Error("Missing URL mapping for data type " + type);
			}
		}
	}
	
	public String resolve(WellDataType t, String agency, String site) {
		String exp = (String) myProps.get(t.name());
		
		ExpressionParser parser = new SpelExpressionParser();

		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("agencyId", agency);
		context.setVariable("featureId", site);
		
		String val = parser.parseExpression(exp,
				new TemplateParserContext("_{","}")).getValue(context, String.class);
		
		if (val.contains(" ")) {
			val = val.replace(" ", "%20");
		}
		
		return val;
	}
	
	public String makeUrl(Specifier spec) {
		return resolve(spec.getTypeID(), spec.getAgencyID(), spec.getFeatureID());
	}
}
