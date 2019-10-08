CREATE TABLE [dbo].AlcoMeasureResults (
	Id INT IDENTITY NOT NULL PRIMARY KEY,
	UserId INT NOT NULL REFERENCES [dbo].[User] (Id),
	Result DECIMAL(9,8) NOT NULL,
	Photo1 INT REFERENCES [dbo].[File] (Id),
	Photo2 INT REFERENCES [dbo].[File] (Id),
	Photo3 INT REFERENCES [dbo].[File] (Id),
	Created DATETIME NOT NULL DEFAULT (GETDATE())
);
