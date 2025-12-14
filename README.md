# Система тестирования и оценки знаний
Проект представляет собой веб-приложение, в котором хранятся тесты с вопросами на разные тематики. Есть возможность пройти тест и получить оценку.
___

## Запуск приложения
```
1. Клонируйте репозиторий: git clone https://github.com/ilmiraKh/TestSystemNaumen.git
2. Создайте файл .env в корне проекта
3. Соберите jar приложения: ./mvnw clean package
4. Соберите Docker образы: docker-compose build
5. Запустите контейнеры: docker-compose up -d
```
___

## Пользовательская документация веб-приложения

Список доступных тестов для всех пользователей.
- GET /tests – просмотр тестов с пагинацией и фильтром по теме и поиску.

Параметры запроса:
- page (int, необязательный, по умолчанию 1) – номер страницы.
- search (String, необязательный) – поисковый запрос по названию теста.
- topicId (Long, необязательный) – фильтр по ID темы. 
- topicName (String, необязательный) – название темы.

Список тем для всех пользователей.
- GET /topics – просмотр тем с пагинацией и поиском по имени.

Параметры запроса:
- page (int, необязательный, по умолчанию 1) – номер страницы.
- search (String, необязательный) – поисковый запрос по названию темы.

Регистрация и вход.
- GET /login – отображение страницы входа.
- GET /registration – форма регистрации.
- POST /registration – создание нового пользователя.

Управление темами для администратора.
- GET /admin/topics/create – форма создания темы.
- POST /admin/topics/create – сохранить новую тему. 
- GET /admin/topics/edit/{id} – форма редактирования темы по ID.
- POST /admin/topics/edit/{id} – обновление темы по ID.
- POST /admin/topics/delete/{id} – удаление темы по ID.

Мониторинг для администратора:
- GET /admin/monitoring - JavaMelody

Управление тестами для преподавателя.
- GET /teacher/tests – список созданных тестов.
- GET /teacher/tests/create/{topicId} – форма создания теста по теме.
- POST /teacher/tests/create/{topicId} – создание нового теста.
- GET /teacher/tests/edit/{id} – форма редактирования теста.
- POST /teacher/tests/edit/{id} – обновление теста.
- POST /teacher/tests/delete/{id} – удаление теста.
- POST /teacher/tests/publish/{id} – публикация теста.

Управление вопросами теста для преподавателя.
- GET /teacher/tests/{testId}/questions – список всех вопросов теста.
- GET /teacher/tests/{testId}/questions/create – форма создания вопроса.
- POST /teacher/tests/{testId}/questions/create – создание нового вопроса.
- GET /teacher/tests/{testId}/questions/edit/{id} – форма редактирования вопроса.
- POST /teacher/tests/{testId}/questions/edit/{id} – обновление вопроса.
- POST /teacher/tests/{testId}/questions/delete/{id} – удаление вопроса.

Просмотр и проверка результатов студентов для преподавателя.
- GET /teacher/results/check/{id} – страница проверки результатов с ручной оценкой.
- POST /teacher/results/check/{id} – отправка оценок за ответы с ручной проверкой.
- GET /teacher/results/test/{testId} – список результатов по тесту.
- GET /teacher/results/{id} – просмотр конкретного результата студента.

Прохождение и отправка тестов студентом.
- GET /student/tests/take/{id} – пройти тест по ID, проверка на уже пройденные тесты.
- POST /student/tests/submit/{id} – отправка ответов на тест, создание результата, редирект на страницу результата.

Просмотр результатов тестов студентом.
- GET /student/results – список результатов студента.
- GET /student/results/{resultId} – просмотр детального результата по ID.
