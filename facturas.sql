DROP TABLE detalles IF EXISTS;
DROP TABLE facturas IF EXISTS;
DROP TABLE persons IF EXISTS;


CREATE TABLE facturas (
  id                INTEGER not null primary key
        GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),
  numero            INTEGER,
  fecha             DATE,
  tpo_pago          VARCHAR(500),
  dcu_cliente       VARCHAR(500),
  nombres           VARCHAR(2000),
  subtotal          DECIMAL(16),
  descuento         DECIMAL(16),
  iva               DECIMAL(16),
  descuento_total   DECIMAL(16),
  impuesto_total    DECIMAL(16),
  total             DECIMAL(16)
);
CREATE INDEX facturas_nunmero ON facturas (numero);

CREATE TABLE productos (
  id                INTEGER not null primary key
        GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),
  nombres           VARCHAR(2000),
  cantidad          INTEGER,
  precio_unitario   DECIMAL
);
CREATE INDEX productos_keys ON productos (nombres);

CREATE TABLE detalles (
  id                INTEGER not null primary key
        GENERATED ALWAYS AS IDENTITY
        (START WITH 1, INCREMENT BY 1),
  factura_id        INTEGER,
  producto_id       INTEGER,
  cantidad          INTEGER,
  precio_unitario   DECIMAL,
  FOREIGN KEY (factura_id) REFERENCES facturas(id),
  FOREIGN KEY (producto_id) REFERENCES productos(id)
);
CREATE INDEX detalles_keys ON detalles (factura_id,producto_id);

