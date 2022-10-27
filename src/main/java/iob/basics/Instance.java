package iob.basics;

import java.io.Serializable;

public class Instance implements Serializable {
	// Fields
	private static final long serialVersionUID = -2732376948187923932L;

	private InstanceId instanceId;

	public Instance() {

	}

	// Constructors
	public Instance(InstanceId instanceId) {
		super();

		setInstanceId(instanceId);
	}

	// Getters & Setters
	public InstanceId getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(InstanceId instanceId) {
		this.instanceId = instanceId;
	}

	// Methods
	@Override
	public String toString() {
		return "Instance [instanceID=" + instanceId + "]";
	}
}