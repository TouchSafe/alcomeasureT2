CREATE TABLE AlcoMeasureResult (
	id INT IDENTITY NOT NULL PRIMARY KEY,
	userId INT NOT NULL CONSTRAINT FK_AlcoMeasureResult_UserId REFERENCES [User] (Id),
	result DECIMAL(9,8) NOT NULL,
	photo1Id INT CONSTRAINT FK_AlcoMeasureResult_Photo1Id REFERENCES [File] (Id),
	photo2Id INT CONSTRAINT FK_AlcoMeasureResult_Photo2Id REFERENCES [File] (Id),
	photo3Id INT CONSTRAINT FK_AlcoMeasureResult_Photo3Id REFERENCES [File] (Id),
	created DATETIME NOT NULL DEFAULT (GETDATE())
);


CREATE TABLE AlcoMeasureDevice (
        id INT IDENTITY NOT NULL PRIMARY KEY,
        created DATETIME NOT NULL DEFAULT (GETDATE()),
         LocationId    int           not null
           constraint FK_AlcoMeasure_Location
             references Location

);
