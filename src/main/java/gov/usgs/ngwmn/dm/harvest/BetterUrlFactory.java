package gov.usgs.ngwmn.dm.harvest;

import gov.usgs.ngwmn.WellDataType;

import java.util.Properties;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class BetterUrlFactory {

	private Properties myProps;
	
	public void setMapping(Properties pp) {
		myProps = pp;
	}
	
	public String resolve(WellDataType t, String agency, String site) {
		String exp = (String) myProps.get(t.name());
		
		ExpressionParser parser = new SpelExpressionParser();

		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("agencyId", agency);
		context.setVariable("featureId", site);
		
		return parser.parseExpression(exp,
				new TemplateParserContext("_{","}")).getValue(context, String.class);
	}
}
