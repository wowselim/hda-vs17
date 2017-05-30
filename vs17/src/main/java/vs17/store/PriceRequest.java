/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package vs17.store;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2017-05-13")
public class PriceRequest implements org.apache.thrift.TBase<PriceRequest, PriceRequest._Fields>, java.io.Serializable, Cloneable, Comparable<PriceRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PriceRequest");

  private static final org.apache.thrift.protocol.TField PRODUCT_FIELD_DESC = new org.apache.thrift.protocol.TField("product", org.apache.thrift.protocol.TType.STRING, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new PriceRequestStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new PriceRequestTupleSchemeFactory();

  public java.lang.String product; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PRODUCT((short)1, "product");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // PRODUCT
          return PRODUCT;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.PRODUCT, new org.apache.thrift.meta_data.FieldMetaData("product", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PriceRequest.class, metaDataMap);
  }

  public PriceRequest() {
  }

  public PriceRequest(
    java.lang.String product)
  {
    this();
    this.product = product;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PriceRequest(PriceRequest other) {
    if (other.isSetProduct()) {
      this.product = other.product;
    }
  }

  public PriceRequest deepCopy() {
    return new PriceRequest(this);
  }

  @Override
  public void clear() {
    this.product = null;
  }

  public java.lang.String getProduct() {
    return this.product;
  }

  public PriceRequest setProduct(java.lang.String product) {
    this.product = product;
    return this;
  }

  public void unsetProduct() {
    this.product = null;
  }

  /** Returns true if field product is set (has been assigned a value) and false otherwise */
  public boolean isSetProduct() {
    return this.product != null;
  }

  public void setProductIsSet(boolean value) {
    if (!value) {
      this.product = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case PRODUCT:
      if (value == null) {
        unsetProduct();
      } else {
        setProduct((java.lang.String)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case PRODUCT:
      return getProduct();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case PRODUCT:
      return isSetProduct();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof PriceRequest)
      return this.equals((PriceRequest)that);
    return false;
  }

  public boolean equals(PriceRequest that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_product = true && this.isSetProduct();
    boolean that_present_product = true && that.isSetProduct();
    if (this_present_product || that_present_product) {
      if (!(this_present_product && that_present_product))
        return false;
      if (!this.product.equals(that.product))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetProduct()) ? 131071 : 524287);
    if (isSetProduct())
      hashCode = hashCode * 8191 + product.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(PriceRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetProduct()).compareTo(other.isSetProduct());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetProduct()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.product, other.product);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("PriceRequest(");
    boolean first = true;

    sb.append("product:");
    if (this.product == null) {
      sb.append("null");
    } else {
      sb.append(this.product);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class PriceRequestStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PriceRequestStandardScheme getScheme() {
      return new PriceRequestStandardScheme();
    }
  }

  private static class PriceRequestStandardScheme extends org.apache.thrift.scheme.StandardScheme<PriceRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PriceRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // PRODUCT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.product = iprot.readString();
              struct.setProductIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, PriceRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.product != null) {
        oprot.writeFieldBegin(PRODUCT_FIELD_DESC);
        oprot.writeString(struct.product);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PriceRequestTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PriceRequestTupleScheme getScheme() {
      return new PriceRequestTupleScheme();
    }
  }

  private static class PriceRequestTupleScheme extends org.apache.thrift.scheme.TupleScheme<PriceRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PriceRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetProduct()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetProduct()) {
        oprot.writeString(struct.product);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PriceRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.product = iprot.readString();
        struct.setProductIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}
