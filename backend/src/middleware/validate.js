import { AppError } from './errorHandler.js';

export const validate = (schema, source = 'body') => {
  return (req, res, next) => {
    const dataToValidate = req[source];
    const { error, value } = schema.validate(dataToValidate, { abortEarly: false, stripUnknown: true });

    if (error) {
      const details = error.details.map((d) => d.message).join('; ');
      return next(new AppError(`Validation failed: ${details}`, 400, 'VALIDATION_ERROR'));
    }

    req[source] = value;
    next();
  };
};
