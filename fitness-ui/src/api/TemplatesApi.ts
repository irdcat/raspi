import TrainingTemplate from '../model/TrainingTemplate';

export default class TemplatesApi {
  static async get(): Promise<Array<TrainingTemplate>> {
    return fetch('/api/templates').then((response) => response.json());
  }

  static async getById(id: string): Promise<TrainingTemplate> {
    return fetch(`/api/templates/${id}`).then((response) => response.json());
  }

  static async add(template: TrainingTemplate): Promise<TrainingTemplate> {
    return fetch('/api/templates', {
      method: 'post',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(template),
    }).then((response) => response.json());
  }

  static async update(id: string, template: TrainingTemplate): Promise<TrainingTemplate> {
    return fetch(`/api/templates/${id}`, {
      method: 'put',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(template),
    }).then((response) => response.json());
  }

  static async delete(id: string): Promise<TrainingTemplate> {
    return fetch(`/api/templates/${id}`, {
      method: 'delete',
      headers: {
        Accept: 'application/json',
      },
    }).then((response) => response.json());
  }
}
