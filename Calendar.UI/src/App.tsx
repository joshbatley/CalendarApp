import { useEffect, useState } from 'react';
import { Box, EdgmontUI } from '@muffled-ui/react'
import { SearchArea, Header, EventArea } from './components';
import { callApi, callPastApi, searchApi } from './api';

const App = () => {
  const [events, setEvents] = useState<CalendarEvents[]>([]);
  const [pastEvents, setPastEvents] = useState<CalendarEvents[]>([]);
  const [searchEvents, setSearchEvents] = useState<CalendarEvents[]>([]);
  const [dates, setDates] = useState<any[]>([]);

  const searchDate = (e: any) => {
    let [start, end] = e;
    if (start && end) {
      setDates([start, end]);
    }
  }

  useEffect(() => {
    const fetchData = async () => {
      const result = await callApi();
      setEvents(result);

      const result2 = await callPastApi();
      setPastEvents(result2);

    };

    fetchData();
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      if (!dates[0] && !dates[1]) {
        return;
      }
      const result = await searchApi(dates[0], dates[1]);
      setSearchEvents(result);
    };

    fetchData();
  }, [dates])

  return (
    <EdgmontUI>
      <Header />

      <Box mx="10" pt="5" display="grid" gridTemplateColumns="1fr 1fr" gridGap="20px">
        <SearchArea onChange={searchDate} searchEvents={searchEvents} />
        <EventArea events={events} pastEvents={pastEvents} />
      </Box>
    </EdgmontUI >
  )
}

export default App
