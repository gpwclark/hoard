// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: hoard-prefix-type.proto

package com.uofantarctica.hoard.protocols;

public final class HoardPrefixType {
  private HoardPrefixType() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface PrefixTypeOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.uofantarctica.hoard.protocols.PrefixType)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required string name = 1;</code>
     */
    boolean hasName();
    /**
     * <code>required string name = 1;</code>
     */
    java.lang.String getName();
    /**
     * <code>required string name = 1;</code>
     */
    com.google.protobuf.ByteString
        getNameBytes();

    /**
     * <code>required .com.uofantarctica.hoard.protocols.PrefixType.ActionType type = 2;</code>
     */
    boolean hasType();
    /**
     * <code>required .com.uofantarctica.hoard.protocols.PrefixType.ActionType type = 2;</code>
     */
    com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType getType();
  }
  /**
   * Protobuf type {@code com.uofantarctica.hoard.protocols.PrefixType}
   */
  public static final class PrefixType extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:com.uofantarctica.hoard.protocols.PrefixType)
      PrefixTypeOrBuilder {
    // Use PrefixType.newBuilder() to construct.
    private PrefixType(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private PrefixType(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final PrefixType defaultInstance;
    public static PrefixType getDefaultInstance() {
      return defaultInstance;
    }

    public PrefixType getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private PrefixType(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              name_ = bs;
              break;
            }
            case 16: {
              int rawValue = input.readEnum();
              com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType value = com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(2, rawValue);
              } else {
                bitField0_ |= 0x00000002;
                type_ = value;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.uofantarctica.hoard.protocols.HoardPrefixType.internal_static_com_uofantarctica_hoard_protocols_PrefixType_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.uofantarctica.hoard.protocols.HoardPrefixType.internal_static_com_uofantarctica_hoard_protocols_PrefixType_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.class, com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.Builder.class);
    }

    public static com.google.protobuf.Parser<PrefixType> PARSER =
        new com.google.protobuf.AbstractParser<PrefixType>() {
      public PrefixType parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new PrefixType(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<PrefixType> getParserForType() {
      return PARSER;
    }

    /**
     * Protobuf enum {@code com.uofantarctica.hoard.protocols.PrefixType.ActionType}
     */
    public enum ActionType
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>DSYNC = 0;</code>
       */
      DSYNC(0, 0),
      /**
       * <code>CHRONOSYNC = 1;</code>
       */
      CHRONOSYNC(1, 1),
      /**
       * <code>REREQUEST = 2;</code>
       */
      REREQUEST(2, 2),
      /**
       * <code>CACHE = 3;</code>
       */
      CACHE(3, 3),
      /**
       * <code>HOARD_DISCOVERY = 4;</code>
       */
      HOARD_DISCOVERY(4, 4),
      ;

      /**
       * <code>DSYNC = 0;</code>
       */
      public static final int DSYNC_VALUE = 0;
      /**
       * <code>CHRONOSYNC = 1;</code>
       */
      public static final int CHRONOSYNC_VALUE = 1;
      /**
       * <code>REREQUEST = 2;</code>
       */
      public static final int REREQUEST_VALUE = 2;
      /**
       * <code>CACHE = 3;</code>
       */
      public static final int CACHE_VALUE = 3;
      /**
       * <code>HOARD_DISCOVERY = 4;</code>
       */
      public static final int HOARD_DISCOVERY_VALUE = 4;


      public final int getNumber() { return value; }

      public static ActionType valueOf(int value) {
        switch (value) {
          case 0: return DSYNC;
          case 1: return CHRONOSYNC;
          case 2: return REREQUEST;
          case 3: return CACHE;
          case 4: return HOARD_DISCOVERY;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<ActionType>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static com.google.protobuf.Internal.EnumLiteMap<ActionType>
          internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<ActionType>() {
              public ActionType findValueByNumber(int number) {
                return ActionType.valueOf(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(index);
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.getDescriptor().getEnumTypes().get(0);
      }

      private static final ActionType[] VALUES = values();

      public static ActionType valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int index;
      private final int value;

      private ActionType(int index, int value) {
        this.index = index;
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:com.uofantarctica.hoard.protocols.PrefixType.ActionType)
    }

    private int bitField0_;
    public static final int NAME_FIELD_NUMBER = 1;
    private java.lang.Object name_;
    /**
     * <code>required string name = 1;</code>
     */
    public boolean hasName() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string name = 1;</code>
     */
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          name_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string name = 1;</code>
     */
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TYPE_FIELD_NUMBER = 2;
    private com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType type_;
    /**
     * <code>required .com.uofantarctica.hoard.protocols.PrefixType.ActionType type = 2;</code>
     */
    public boolean hasType() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required .com.uofantarctica.hoard.protocols.PrefixType.ActionType type = 2;</code>
     */
    public com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType getType() {
      return type_;
    }

    private void initFields() {
      name_ = "";
      type_ = com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType.DSYNC;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasName()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasType()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getNameBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeEnum(2, type_.getNumber());
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getNameBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(2, type_.getNumber());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code com.uofantarctica.hoard.protocols.PrefixType}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.uofantarctica.hoard.protocols.PrefixType)
        com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixTypeOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.uofantarctica.hoard.protocols.HoardPrefixType.internal_static_com_uofantarctica_hoard_protocols_PrefixType_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.uofantarctica.hoard.protocols.HoardPrefixType.internal_static_com_uofantarctica_hoard_protocols_PrefixType_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.class, com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.Builder.class);
      }

      // Construct using com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        name_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        type_ = com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType.DSYNC;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.uofantarctica.hoard.protocols.HoardPrefixType.internal_static_com_uofantarctica_hoard_protocols_PrefixType_descriptor;
      }

      public com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType getDefaultInstanceForType() {
        return com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.getDefaultInstance();
      }

      public com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType build() {
        com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType buildPartial() {
        com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType result = new com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.name_ = name_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.type_ = type_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType) {
          return mergeFrom((com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType other) {
        if (other == com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.getDefaultInstance()) return this;
        if (other.hasName()) {
          bitField0_ |= 0x00000001;
          name_ = other.name_;
          onChanged();
        }
        if (other.hasType()) {
          setType(other.getType());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasName()) {
          
          return false;
        }
        if (!hasType()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object name_ = "";
      /**
       * <code>required string name = 1;</code>
       */
      public boolean hasName() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string name = 1;</code>
       */
      public java.lang.String getName() {
        java.lang.Object ref = name_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            name_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string name = 1;</code>
       */
      public com.google.protobuf.ByteString
          getNameBytes() {
        java.lang.Object ref = name_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          name_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string name = 1;</code>
       */
      public Builder setName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        name_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string name = 1;</code>
       */
      public Builder clearName() {
        bitField0_ = (bitField0_ & ~0x00000001);
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      /**
       * <code>required string name = 1;</code>
       */
      public Builder setNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        name_ = value;
        onChanged();
        return this;
      }

      private com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType type_ = com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType.DSYNC;
      /**
       * <code>required .com.uofantarctica.hoard.protocols.PrefixType.ActionType type = 2;</code>
       */
      public boolean hasType() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required .com.uofantarctica.hoard.protocols.PrefixType.ActionType type = 2;</code>
       */
      public com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType getType() {
        return type_;
      }
      /**
       * <code>required .com.uofantarctica.hoard.protocols.PrefixType.ActionType type = 2;</code>
       */
      public Builder setType(com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000002;
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required .com.uofantarctica.hoard.protocols.PrefixType.ActionType type = 2;</code>
       */
      public Builder clearType() {
        bitField0_ = (bitField0_ & ~0x00000002);
        type_ = com.uofantarctica.hoard.protocols.HoardPrefixType.PrefixType.ActionType.DSYNC;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:com.uofantarctica.hoard.protocols.PrefixType)
    }

    static {
      defaultInstance = new PrefixType(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:com.uofantarctica.hoard.protocols.PrefixType)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_uofantarctica_hoard_protocols_PrefixType_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_uofantarctica_hoard_protocols_PrefixType_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\027hoard-prefix-type.proto\022!com.uofantarc" +
      "tica.hoard.protocols\"\272\001\n\nPrefixType\022\014\n\004n" +
      "ame\030\001 \002(\t\022F\n\004type\030\002 \002(\01628.com.uofantarct" +
      "ica.hoard.protocols.PrefixType.ActionTyp" +
      "e\"V\n\nActionType\022\t\n\005DSYNC\020\000\022\016\n\nCHRONOSYNC" +
      "\020\001\022\r\n\tREREQUEST\020\002\022\t\n\005CACHE\020\003\022\023\n\017HOARD_DI" +
      "SCOVERY\020\004"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_uofantarctica_hoard_protocols_PrefixType_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_uofantarctica_hoard_protocols_PrefixType_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_uofantarctica_hoard_protocols_PrefixType_descriptor,
        new java.lang.String[] { "Name", "Type", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}