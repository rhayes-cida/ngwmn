package gov.usgs.ngwmn.admin.stats;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Representation of data tree for JIT visualization.
 * @author rhayes
 *
 */
@JsonSerialize(include=Inclusion.NON_NULL)
public class JitTree {
	public String id;
	public String name;
	
	public JitTree(String nm, int r) {
		id = String.valueOf(System.identityHashCode(this));
		name = nm;
		data.area = r;
	}
	
	public JitTree(String nm) {
		id = String.valueOf(System.identityHashCode(this));
		name = nm;
		data.area = 0;
	}
	
	public synchronized JitTree addChild(JitTree k) {
		if (children == null) {
			children = new ArrayList<JitTree>(3);
		}
		children.add(k);
		data.area += k.data.area;
		return this;
	}
	
	public class JitData {
		// it seems that JIT Node.dim is used to calculate the size of the child in the icicle tree
		@JsonProperty("$dim")
		public int area;
		
		// public int dim;
		// public String color;
	}
	
	public final JitData data = new JitData();
	
	public List<JitTree> children;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JitTree [name=");
		builder.append(name);
		builder.append(", area=");
		builder.append(data.area);
		builder.append("]");
		return builder.toString();
	}
	
	
}
